package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.annotation.PostConstruct;
import javax.interceptor.Interceptor;

@Interceptor
@InterceptorStereotype
public class InterceptorWithPostConstructorBroken {

    @PostConstruct
    public void initialize() {
    }
}