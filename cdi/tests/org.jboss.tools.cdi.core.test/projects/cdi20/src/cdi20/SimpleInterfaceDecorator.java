package cdi20;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

@Decorator
@Dependent
public abstract class SimpleInterfaceDecorator implements SimpleInterface {

	@Inject
	@Delegate
	@Any
	private SimpleInterface simpleInterface;

	public SimpleInterfaceDecorator() {
		// TODO Auto-generated constructor stub
	}
	
	public void withObserver(@ObservesAsync PaymentEvent event) {
		
	}

}
