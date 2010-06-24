package org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public abstract class TimestampParametedLoggerBroken extends Clazz<String> {

   @Inject @Delegate private Clazz<Logger> logger;

}