package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({})
@Documented
@Qualifier
public @interface TestQualifier4 {

}
