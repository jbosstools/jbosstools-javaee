package org.jboss.jsr299.tck.tests.jbt.validation.specialization;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

@Decorator
@Specializes
public class SpecializingDecoratorBroken extends Farmer {

	@Inject @Delegate @Any Object logger;
}