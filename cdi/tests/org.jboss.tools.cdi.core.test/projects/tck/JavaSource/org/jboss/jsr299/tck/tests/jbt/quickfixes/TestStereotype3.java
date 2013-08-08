package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;

@Retention(RUNTIME)
@Stereotype
@ApplicationScoped
@Named
public @interface TestStereotype3 {

}
