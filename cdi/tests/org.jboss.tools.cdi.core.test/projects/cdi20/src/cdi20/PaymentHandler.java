package cdi20;

import java.io.Serializable;

import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@SessionScoped
public class PaymentHandler implements Serializable {
	
	@Inject
	private Event<PaymentEvent> event;
	
	public void abcd() {
		event.fire(new PaymentEvent());
	}

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Inject
	public PaymentHandler(@ObservesAsync PaymentEvent event) {
		
	}
	
	public void priorityWithObserves(@Observes @Credit @Priority(500) PaymentEvent event) {
		
	}
	
	public void priorityWithObservesAsync(@ObservesAsync @Credit @Priority(600) PaymentEvent event) {
		
	}
	
	public void multipleObservesAsync(@ObservesAsync PaymentEvent event, @ObservesAsync PaymentEvent event2) {
		
	}
	
	@Inject
	public void observerWithAsync(@ObservesAsync PaymentEvent event) {
		
	}
	
	@Produces
	public PaymentEvent producerAndObserver(@ObservesAsync PaymentEvent event) {
		return new PaymentEvent();
	}
	
	public void observerDisposes(@ObservesAsync @Disposes PaymentEvent event) {
		
	}
	
	public void paramWithObserverAndObserverAsync(@Observes @ObservesAsync PaymentEvent event) {
		
	}
	
	public void paramWithObserverAndObserverAsync(@Observes PaymentEvent event, @ObservesAsync PaymentEvent event2) {
		
	}
	
	public void asyncObserver(@ObservesAsync PaymentEvent event) {
		
	}
	
	public void asyncObserverCredit(@ObservesAsync @Credit PaymentEvent event) {
	 
	}
	
	public void asyncObserverDebit(@ObservesAsync @Debit PaymentEvent event) {
		
	}
}