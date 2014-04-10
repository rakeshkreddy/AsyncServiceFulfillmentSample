package com.service;

import com.async.task.*;
import com.google.common.base.Stopwatch;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class ServiceFulfillmentWithRxJava {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        final Scheduler scheduler = Schedulers.executor(executor);

        //Run Task A & Task B in parallel
        Future<String> serviceA_Future = executor.submit(new CallToRemoteServiceA());
        Future<Integer> serviceB_Future = executor.submit(new CallToRemoteServiceB());

        Observable<String> serviceA_Observable = Observable.from(serviceA_Future);
        Observable<ServiceResult> serviceC_Observable = serviceA_Observable.flatMap(new Func1<String, Observable<ServiceResult>>() {
            @Override
            public Observable<ServiceResult> call(String s) {
                System.out.println("C Observed from A: " + s);
                Future<ServiceResult> serviceC_Future = executor.submit(new CallToRemoteServiceC(s));
                return Observable.from(serviceC_Future);
            }
        });

        Observable<Integer> serviceB_Observable = Observable.from(serviceB_Future);
        Observable<ServiceResult> serviceD_Observable = serviceB_Observable.flatMap(new Func1<Integer, Observable<ServiceResult>>() {
            @Override
            public Observable<ServiceResult> call(Integer integer) {
                System.out.println("D Observed from B: " + integer);
                Future<ServiceResult> serviceD_Future = executor.submit(new CallToRemoteServiceD(integer));
                return Observable.from(serviceD_Future);
            }
        });

        Observable<ServiceResult> serviceE_Observable = serviceB_Observable.flatMap(new Func1<Integer, Observable<ServiceResult>>() {
            @Override
            public Observable<ServiceResult> call(Integer integer) {
                System.out.println("E Observed from B: " + integer);
                Future<ServiceResult> serviceE_Future = executor.submit(new CallToRemoteServiceE(integer));
                return Observable.from(serviceE_Future);
            }
        });

        System.out.println("Waiting for final result");
        Observable.zip(serviceC_Observable, serviceD_Observable, serviceE_Observable, new Func3<ServiceResult, ServiceResult, ServiceResult, Map<String, ServiceResult>>() {
            @Override
            public Map<String, ServiceResult> call(ServiceResult c, ServiceResult d, ServiceResult e) {
                Map<String, ServiceResult> map = new HashMap<String, ServiceResult>();
                map.put("C", c);
                map.put("D", d);
                map.put("E", e);
                return map;
            }
        }).subscribe(new Action1<Map<String, ServiceResult>>() {
            @Override
            public void call(Map<String, ServiceResult> map) {
                System.out.println("Final Result C: "+map.get("C") + ", D:"+map.get("D")+", E:"+map.get("E"));
            }
        });

        executor.shutdown();
        System.out.println("Finished Execution "+stopwatch);
    }
}
