package com.async.task;

import com.service.ServiceResult;

import java.util.concurrent.Callable;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class CallToRemoteServiceE implements Callable<ServiceResult> {

    private final Integer dependencyFromB;

    public CallToRemoteServiceE(Integer dependencyFromB) {
        this.dependencyFromB = dependencyFromB;
    }

    @Override
    public ServiceResult call() throws Exception {
        System.out.println("E started");
        // simulate fetching data from remote service
        Thread.sleep(1000);
        System.out.println("E responding");
        ServiceResult result = new ServiceResult();
        result.serviceEResult = 5000 + dependencyFromB;
        return result;
    }
}