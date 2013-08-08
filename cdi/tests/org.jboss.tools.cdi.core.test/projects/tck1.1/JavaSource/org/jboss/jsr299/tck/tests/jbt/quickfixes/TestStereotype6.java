package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;

@Retention(RUNTIME)
@Stereotype
@Target({TYPE, METHOD, FIELD})
@ApplicationScoped
@Named
@Typed
public @interface TestStereotype6 {

}
