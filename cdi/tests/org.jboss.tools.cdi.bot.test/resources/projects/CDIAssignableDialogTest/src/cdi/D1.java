package cdi;

import java.util.Set;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

@Decorator
public abstract class D1 extends AbstractManager implements Set<String> {

	@Inject
	@Delegate
	@Any
	private Set<String> set;

	public D1() {
		// TODO Auto-generated constructor stub
	}
	
}
