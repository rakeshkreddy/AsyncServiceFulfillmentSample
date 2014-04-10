package com.async.task;

import com.service.ServiceResult;

import java.util.concurrent.Callable;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class CallToRemoteServiceC implements Callable<ServiceResult> {

    private final String dependencyFromA;

    public CallToRemoteServiceC(String dependencyFromA) {
        this.dependencyFromA = dependencyFromA;
    }

    @Override
    public ServiceResult call() throws Exception {
        System.out.println("C started");
        // simulate fetching data from remote service
        Thread.sleep(1000);
        System.out.println("C responding");
        ServiceResult result = new ServiceResult();
        result.serviceCResult = "responseC_" + dependencyFromA;
        return result;
    }
}
