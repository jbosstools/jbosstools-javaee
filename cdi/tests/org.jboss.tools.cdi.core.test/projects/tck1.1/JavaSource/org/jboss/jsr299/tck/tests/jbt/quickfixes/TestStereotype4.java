package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;

@Retention(RUNTIME)
@Stereotype
@Target({})
@ApplicationScoped
@Named
public @interface TestStereotype4 {

}
