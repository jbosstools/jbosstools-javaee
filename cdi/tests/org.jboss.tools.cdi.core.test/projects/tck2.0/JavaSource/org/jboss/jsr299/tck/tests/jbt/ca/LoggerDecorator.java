package org.jboss.jsr299.tck.tests.jbt.ca;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.jsr299.tck.tests.decorators.definition.Logger;

@Decorator
public abstract class LoggerDecorator implements Logger {

	@Inject
	@Delegate
	@Any
	private Logger logger;

	public LoggerDecorator() {
	}

	@Override
	public void log(String string) {
		logger.log(string);
	}
}