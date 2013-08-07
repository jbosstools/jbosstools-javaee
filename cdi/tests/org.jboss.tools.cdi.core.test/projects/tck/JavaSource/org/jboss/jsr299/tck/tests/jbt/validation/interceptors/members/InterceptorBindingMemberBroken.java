package org.jboss.jsr299.tck.tests.jbt.validation.interceptors.members;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@Inherited
@InterceptorBinding
@Target( { TYPE, METHOD })
@Retention(RUNTIME)
@interface InterceptorBindingMemberBroken {
	SimpleAnnotation memberBroken();
	@Nonbinding SimpleAnnotation member();
	String[] pricesBroken();
	@Nonbinding String[] prices();
}