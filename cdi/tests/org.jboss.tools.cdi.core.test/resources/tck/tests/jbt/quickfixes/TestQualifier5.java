package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Documented
@Qualifier
public @interface TestQualifier5 {
	AAnnotation abc();
}
