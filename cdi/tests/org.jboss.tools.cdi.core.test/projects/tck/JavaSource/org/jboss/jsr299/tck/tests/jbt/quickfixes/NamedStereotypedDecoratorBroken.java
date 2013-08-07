package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

@Decorator
@NamedStereotype
public class NamedStereotypedDecoratorBroken {

	@Inject @Delegate @Any Object logger;
}