package com.service;

import com.google.common.base.Objects;

/**
 * Created by Rakesh on 4/10/2014.
 */
public class ServiceResult {
    public String serviceCResult;
    public Integer serviceDResult;
    public Integer serviceEResult;

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("serviceCResult", serviceCResult)
                .add("serviceDResult", serviceDResult)
                .add("serviceEResult", serviceEResult).omitNullValues()
                .toString();
    }
}
