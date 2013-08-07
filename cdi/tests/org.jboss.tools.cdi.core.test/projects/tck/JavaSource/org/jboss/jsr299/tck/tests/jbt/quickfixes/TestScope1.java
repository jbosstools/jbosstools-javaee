package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import javax.inject.Scope;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Scope
@Inherited
@Target({ TYPE, METHOD, FIELD })
@Documented
public @interface TestScope1 {

}
