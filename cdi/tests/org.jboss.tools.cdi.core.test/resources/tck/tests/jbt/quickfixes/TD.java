package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.lang.model.element.Element;

@Decorator
@javax.inject.Named("aaa")
public abstract class TD implements Element {

	@Inject
	@Delegate
	@Any
	private Element element;
}
