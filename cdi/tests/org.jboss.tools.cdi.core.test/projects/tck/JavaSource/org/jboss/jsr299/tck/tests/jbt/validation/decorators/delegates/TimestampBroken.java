package org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class TimestampBroken implements IClazz<Logger> {

	@Inject @Delegate private IClazz<String> logger;
}