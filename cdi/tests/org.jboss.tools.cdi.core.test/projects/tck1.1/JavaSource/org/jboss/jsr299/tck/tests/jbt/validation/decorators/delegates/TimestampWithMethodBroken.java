package org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class TimestampWithMethodBroken implements IClazz<String> {

	@Inject void setInt(@Delegate IClazz<Logger> logger) {
	}
}