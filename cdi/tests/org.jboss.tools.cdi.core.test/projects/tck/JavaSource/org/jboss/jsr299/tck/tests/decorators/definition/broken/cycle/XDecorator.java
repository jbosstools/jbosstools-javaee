package org.jboss.jsr299.tck.tests.decorators.definition.broken.cycle;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class XDecorator extends XDecorator {
	@Inject @Delegate XDecorator decorator;

}
