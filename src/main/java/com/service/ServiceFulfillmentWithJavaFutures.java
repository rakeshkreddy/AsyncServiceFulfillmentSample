package com.service;

import com.async.task.*;
import com.google.common.base.Stopwatch;

import java.util.concurrent.*;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class ServiceFulfillmentWithJavaFutures {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            //runApproachOne();
            //runApproachTwo();
            runApproachThree();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        System.out.println("Finished Execution "+stopwatch);
    }

    private static void runApproachOne() throws ExecutionException, InterruptedException {
        //Task C executes with the result from Task A
        Future<String> serviceA_Future = executor.submit(new CallToRemoteServiceA());
        Future<ServiceResult> serviceC_Future = executor.submit(new CallToRemoteServiceC(serviceA_Future.get()));

        //Even though work below has no dependency on Task A the following cannot proceed until TaskA completes
        Future<Integer> serviceB_Future = executor.submit(new CallToRemoteServiceB());
        //D & E execute with the result from B
        Future<ServiceResult> serviceD_Future = executor.submit(new CallToRemoteServiceD(serviceB_Future.get()));
        Future<ServiceResult> serviceE_Future = executor.submit(new CallToRemoteServiceE(serviceB_Future.get()));

        System.out.println("Waiting for final result");
        System.out.println("Final Result C: "+serviceC_Future.get() + ", D:"+serviceD_Future.get()+", E:"+serviceE_Future.get());
    }

    private static void runApproachTwo() throws ExecutionException, InterruptedException {
        //Run Task A & Task B in parallel
        Future<String> serviceA_Future = executor.submit(new CallToRemoteServiceA());
        Future<Integer> serviceB_Future = executor.submit(new CallToRemoteServiceB());

        //Task C executes with the result from Task A
        Future<ServiceResult> serviceC_Future = executor.submit(new CallToRemoteServiceC(serviceA_Future.get()));

        //Even though work below has no dependency on Task A it cannot proceed until TaskA completes
        //D & E execute with the result from B
        Future<ServiceResult> serviceD_Future = executor.submit(new CallToRemoteServiceD(serviceB_Future.get()));
        Future<ServiceResult> serviceE_Future = executor.submit(new CallToRemoteServiceE(serviceB_Future.get()));

        System.out.println("Waiting for final result");
        System.out.println("Final Result C: "+serviceC_Future.get() + ", D:"+serviceD_Future.get()+", E:"+serviceE_Future.get());
    }

    private static void runApproachThree() throws ExecutionException, InterruptedException {
        //Run Task A & Task B in parallel
        final Future<String> serviceA_Future = executor.submit(new CallToRemoteServiceA());
        Future<Integer> serviceB_Future = executor.submit(new CallToRemoteServiceB());

        //execute Task C in separate thread
        Future<ServiceResult> serviceC_Future = executor.submit(new Callable<ServiceResult>() {
            @Override
            public ServiceResult call() throws Exception {
                return new CallToRemoteServiceC(serviceA_Future.get()).call();
            }
        });

        //Even though work below has no dependency on Task A it cannot proceed until TaskA completes
        //D & E execute with the result from B
        Future<ServiceResult> serviceD_Future = executor.submit(new CallToRemoteServiceD(serviceB_Future.get()));
        Future<ServiceResult> serviceE_Future = executor.submit(new CallToRemoteServiceE(serviceB_Future.get()));

        System.out.println("Waiting for final result");
        System.out.println("Final Result C: "+serviceC_Future.get() + ", D:"+serviceD_Future.get()+", E:"+serviceE_Future.get());
    }
}
