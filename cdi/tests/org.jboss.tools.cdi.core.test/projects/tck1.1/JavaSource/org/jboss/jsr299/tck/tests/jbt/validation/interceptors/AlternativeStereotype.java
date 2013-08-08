package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

@Stereotype
@Target( { TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Inherited
@Alternative
@interface AlternativeStereotype {

}