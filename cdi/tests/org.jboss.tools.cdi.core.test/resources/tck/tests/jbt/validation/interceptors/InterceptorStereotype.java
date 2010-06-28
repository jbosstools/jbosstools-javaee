package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

@Stereotype
@CatInterceptorBinding
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface InterceptorStereotype {

}