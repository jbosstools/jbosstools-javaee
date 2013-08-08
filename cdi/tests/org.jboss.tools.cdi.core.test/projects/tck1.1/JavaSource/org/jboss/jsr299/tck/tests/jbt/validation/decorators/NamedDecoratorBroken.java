package org.jboss.jsr299.tck.tests.jbt.validation.decorators;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.inject.Named;

@Decorator
@Named
public class NamedDecoratorBroken {

	@Inject @Delegate @Any Object logger;
}