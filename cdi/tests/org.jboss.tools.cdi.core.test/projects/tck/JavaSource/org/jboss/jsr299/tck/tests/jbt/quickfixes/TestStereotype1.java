package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;

@Stereotype
@Target( { TYPE })
@ApplicationScoped
@Named
public @interface TestStereotype1 {

}
