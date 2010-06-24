package org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public abstract class TimestampLoggerBroken extends Clazz<Logger> implements Logger {

   @Inject @Delegate private Clazz<String> logger;

}