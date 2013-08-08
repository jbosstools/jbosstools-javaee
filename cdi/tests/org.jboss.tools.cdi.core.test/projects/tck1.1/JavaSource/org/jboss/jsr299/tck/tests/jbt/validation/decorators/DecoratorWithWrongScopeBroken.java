package org.jboss.jsr299.tck.tests.jbt.validation.decorators;

import javax.decorator.Decorator;
import javax.enterprise.context.ApplicationScoped;

@Decorator
@ApplicationScoped
public class DecoratorWithWrongScopeBroken {

}