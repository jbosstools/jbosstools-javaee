package cdi20;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.ObservesAsync;

@Dependent
public class DependentBean {

	
	public void dependentObserverAsync(@ObservesAsync(notifyObserver=IF_EXISTS) PaymentEvent event) {
		
	}
}
