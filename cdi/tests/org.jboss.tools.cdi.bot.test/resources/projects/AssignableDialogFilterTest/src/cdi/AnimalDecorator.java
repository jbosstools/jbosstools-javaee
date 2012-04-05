package cdi;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

@Decorator
public abstract class AnimalDecorator implements Animal {

	@Inject
	@Delegate
	@Any
	private Animal animal;

	public AnimalDecorator() {
		// TODO Auto-generated constructor stub
	}

}
