package org.jboss.jsr299.tck.tests.jbt.validation.annotations.stereotype.broken;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;

@Stereotype
@Target( { TYPE, FIELD })
@Retention(RUNTIME)
@ApplicationScoped
@Named
@interface FishStereotype_WrongTarget {

}