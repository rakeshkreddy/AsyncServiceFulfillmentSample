package com.async.task;

import com.service.ServiceResult;

import java.util.concurrent.Callable;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class CallToRemoteServiceD implements Callable<ServiceResult> {

    private final Integer dependencyFromB;

    public CallToRemoteServiceD(Integer dependencyFromB) {
        this.dependencyFromB = dependencyFromB;
    }

    @Override
    public ServiceResult call() throws Exception {
        System.out.println("D started");
        // simulate fetching data from remote service
        Thread.sleep(1000);
        System.out.println("D responding");
        ServiceResult result = new ServiceResult();
        result.serviceDResult = 40 + dependencyFromB;
        return result;
    }
}