package com.async.task;

import java.util.concurrent.Callable;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class CallToRemoteServiceB implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("B started");
        // simulate fetching data from remote service
        Thread.sleep(500);
        System.out.println("B responding");
        return 100;
    }
}