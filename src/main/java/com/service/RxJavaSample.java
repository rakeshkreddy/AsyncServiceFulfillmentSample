package com.service;

import javafx.collections.ObservableList;
import rx.*;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.Observers;
import rx.observers.Subscribers;
import rx.schedulers.ExecutorScheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Rakesh on 4/4/2014.
 */
public class RxJavaSample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        final Scheduler scheduler = Schedulers.executor(executor);

        Observable<String> taskA = getTaskA(1000);
        taskA.subscribeOn(scheduler);

        Observable<String> taskB = getTaskB(1000);
        taskB.subscribeOn(scheduler);

        //handle Task A response
        final Observable<Long> taskCResult =  taskA.flatMap(new Func1<String, Observable<Long>>() {
            @Override
            public Observable<Long> call(String s) {
                System.out.println("Task C executing from result from Task A: "+s);
                return Observable.just(new Long(100));
            }
        });

        //handle Task B response
        Observable<Long> taskD_E_Result = taskB.flatMap(new Func1<String, Observable<Long>>() {
            @Override
            public Observable<Long> call(String s) {
                //run Task D
                Observable<Long> taskD = getTaskD(1000,s);
                taskD.subscribeOn(scheduler);

                //run Task E
                Observable<Long> taskE = getTaskE(500,s);
                taskE.subscribeOn(scheduler);

                return Observable.zip(taskD,taskE,new Func2<Long, Long, Long>(){
                    @Override
                    public Long call(Long aLong, Long aLong2) {
                        return new Long(aLong + aLong2);
                    }
                });
            }
        });

        //F has to zip on (B,E) & C
        Observable<Long> results = Observable.zip(taskCResult.takeLast(1),taskD_E_Result.takeLast(1),new Func2<Long, Long, Long>() {
            @Override
            public Long call(Long aLong, Long aLong2) {
                System.out.println("Final result is");
                return new Long(aLong + aLong2);
            }
        });

        results.subscribe(new Action1<Long>() {
            @Override
            public void call(Long s) {
                System.out.println("Final result is :"+s);
            }
        });
        System.out.println("Finished execution");

    }

    private static Observable<String> getTaskB(final int i) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                System.out.println("Task B Started");
                try {
                    Thread.currentThread().sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task B Completed");
                //inform the subscribers about the response
                subscriber.onNext("200");
                //mark as completed
                subscriber.onCompleted();
            }
        });
    }

    private static Observable<Long> getTaskD(final int i, final String s) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                System.out.println("Task D Started with value " + s);
                try {
                    Thread.currentThread().sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task D Completed");
                //inform the subscribers about the response
                subscriber.onNext(new Long(s));
                //mark as completed
                subscriber.onCompleted();
            }
        });
    }

    private static Observable<Long> getTaskE(final int i, final String s) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                System.out.println("Task E Started with value " + s);
                try {
                    Thread.currentThread().sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task E Completed");
                //inform the subscribers about the response
                subscriber.onNext(new Long(s));
                //mark as completed
                subscriber.onCompleted();
            }
        });
    }

    private static Observable<String> getTaskC(final int i, final String s) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                System.out.println("Task C Started with value " + s);
                try {
                    Thread.currentThread().sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task C Completed");
                //inform the subscribers about the response
                subscriber.onNext("Task_C_Return_Value");
                //mark as completed
                subscriber.onCompleted();
            }
        });
    }

    private static Observable<String> getTaskA(final int i) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                System.out.println("Task A Started");
                try {
                    Thread.currentThread().sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task A Completed");
                //inform the subscribers about the response
                subscriber.onNext("Task_A_Return_Value");
                //mark as completed
                subscriber.onCompleted();
            }
        });
    }

}
