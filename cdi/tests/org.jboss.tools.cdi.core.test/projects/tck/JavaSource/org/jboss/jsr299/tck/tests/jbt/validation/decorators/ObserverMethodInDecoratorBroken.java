package org.jboss.jsr299.tck.tests.jbt.validation.decorators;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

@Decorator
public class ObserverMethodInDecoratorBroken {

	@Inject @Delegate @Any Object logger;

	public void observeSomeEvent(@Observes String someEvent) {
	}
}