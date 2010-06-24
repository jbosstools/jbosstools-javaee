package org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public abstract class TimestampLoggerWithMethod implements Logger, EnhancedLogger {

	@Inject void setInt(@Delegate MockLogger logger) {
		
	}
}