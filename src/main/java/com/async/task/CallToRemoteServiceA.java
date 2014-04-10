package com.async.task;

import java.util.concurrent.Callable;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class CallToRemoteServiceA implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("A started");
        // simulate fetching data from remote service
        Thread.sleep(1000);
        System.out.println("A responding");
        return "responseA";
    }
}
