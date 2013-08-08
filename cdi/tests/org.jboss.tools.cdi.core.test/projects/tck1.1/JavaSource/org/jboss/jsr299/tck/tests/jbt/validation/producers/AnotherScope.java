package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;

@Target( { TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
@NormalScope
@interface AnotherScope {

}