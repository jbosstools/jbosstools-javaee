package org.jboss.jsr299.tck.tests.jbt.validation.target;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Stereotype
@StereotypeWTypeTarget
@InterceptorBindingWTypeTarget
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface StereotypeOk {

}