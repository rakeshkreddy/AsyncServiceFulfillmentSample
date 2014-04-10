package com.service;

import com.async.task.*;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.*;
import com.service.ServiceResult;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by Rakesh on 4/9/2014.
 */
public class ServiceFulfillmentWithGuava {

    final static ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        //Function to capture response from A
        AsyncFunction<String, ServiceResult> serviceA_Result_Handler = getServiceAResultHandler();

        //Function to capture response from B
        AsyncFunction<Integer, ServiceResult> serviceB_Result_Handler_For_D = getServiceBResultHandlerForD();

        //Function to capture response from B
        AsyncFunction<Integer, ServiceResult> serviceB_Result_Handler_For_E = getServiceBResultHandlerForE();

        System.out.println("Starting to run Tasks");
        //start A
        ListenableFuture<String> serviceA_Future = executor.submit(new CallToRemoteServiceA());
        //start B
        ListenableFuture<Integer> serviceB_Future = executor.submit(new CallToRemoteServiceB());
        //orchestrate the dependencies
        ListenableFuture<ServiceResult> serviceC_Result = Futures.transform(serviceA_Future, serviceA_Result_Handler, executor);
        ListenableFuture<ServiceResult> serviceD_Result = Futures.transform(serviceB_Future, serviceB_Result_Handler_For_D, executor);
        ListenableFuture<ServiceResult> serviceE_Result = Futures.transform(serviceB_Future, serviceB_Result_Handler_For_E, executor);

        //group all the service responses
        ListenableFuture<List<ServiceResult>> finalResult =   Futures.allAsList(serviceC_Result, serviceD_Result, serviceE_Result);

        try {
            System.out.println("Waiting for final result");
            //Wait for all the services to respond
            //NOTE: this is blocking
            List<ServiceResult> allServicesResult  = finalResult.get();

            System.out.println("Final result "+allServicesResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println("Finished Execution "+ stopwatch);
    }

    private static AsyncFunction<Integer, ServiceResult> getServiceBResultHandlerForE() {
        return new AsyncFunction<Integer, ServiceResult>() {
                @Override
                public ListenableFuture<ServiceResult> apply(final Integer s) {
                    System.out.println("E Observed from B: " + s);
                    ListenableFuture<ServiceResult> serviceE_Future = executor.submit(new CallToRemoteServiceE(s));
                    return serviceE_Future;
                }
            };
    }

    private static AsyncFunction<Integer, ServiceResult> getServiceBResultHandlerForD() {
        return new AsyncFunction<Integer, ServiceResult>() {
                @Override
                public ListenableFuture<ServiceResult> apply(final Integer s) {
                    System.out.println("D Observed from B: " + s);
                    ListenableFuture<ServiceResult> serviceD_Future = executor.submit(new CallToRemoteServiceD(s));
                    return serviceD_Future;
                }
            };
    }

    private static AsyncFunction<String, ServiceResult> getServiceAResultHandler() {
        return new AsyncFunction<String, ServiceResult>() {
                @Override
                public ListenableFuture<ServiceResult> apply(final String s) {
                    System.out.println("C Observed from A: " + s);
                    ListenableFuture<ServiceResult> serviceC_Future = executor.submit(new CallToRemoteServiceC(s));
                    return serviceC_Future;
                }
            };
    }

}
