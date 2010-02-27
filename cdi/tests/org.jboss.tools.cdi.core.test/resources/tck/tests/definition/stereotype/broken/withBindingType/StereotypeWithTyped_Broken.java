package org.jboss.jsr299.tck.tests.definition.stereotype.broken.withBindingType;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;

@Stereotype
@Target( { TYPE })
@Retention(RUNTIME)
@Typed(String.class)
// This file is used to test JBT and it is missing in TCK.
@interface StereotypeWithTyped_Broken {
}