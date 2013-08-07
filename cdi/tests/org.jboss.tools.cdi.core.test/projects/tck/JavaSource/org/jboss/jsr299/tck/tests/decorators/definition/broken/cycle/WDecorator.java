package org.jboss.jsr299.tck.tests.decorators.definition.broken.cycle;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class WDecorator extends YDecorator {
	@Inject @Delegate WDecorator decorator;

}
