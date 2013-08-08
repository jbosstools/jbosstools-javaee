package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;

import javax.inject.Scope;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Scope
@Inherited
@Documented
public @interface TestScope3 {

}
