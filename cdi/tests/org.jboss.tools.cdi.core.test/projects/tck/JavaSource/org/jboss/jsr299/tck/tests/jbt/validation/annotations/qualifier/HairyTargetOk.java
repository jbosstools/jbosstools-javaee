package org.jboss.jsr299.tck.tests.jbt.validation.annotations.qualifier.broken;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Target( { FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
@Qualifier
@Inherited
@interface HairyTargetOk {

   public boolean clipped();
}