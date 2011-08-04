package org.jboss.jsr299.tck.tests.jbt.lookup.duplicateName;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.inject.Named;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Stereotype
@Named
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface TestS {

}
