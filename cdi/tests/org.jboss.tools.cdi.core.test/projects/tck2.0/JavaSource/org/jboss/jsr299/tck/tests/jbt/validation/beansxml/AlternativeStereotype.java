package org.jboss.jsr299.tck.tests.jbt.validation.beansxml;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Stereotype
@Alternative
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface AlternativeStereotype {

}