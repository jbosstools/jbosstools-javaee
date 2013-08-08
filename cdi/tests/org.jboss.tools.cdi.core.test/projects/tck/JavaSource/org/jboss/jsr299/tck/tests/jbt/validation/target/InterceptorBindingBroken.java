package org.jboss.jsr299.tck.tests.jbt.validation.target;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Inherited
@InterceptorBindingWTypeTarget
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface InterceptorBindingBroken {

}