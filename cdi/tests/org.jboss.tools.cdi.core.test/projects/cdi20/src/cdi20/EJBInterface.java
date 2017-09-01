package cdi20;

import javax.ejb.Local;

@Local
public interface EJBInterface {
	
	public void someMethod();
	public void someMethod1(PaymentEvent event);

}
