package cdi20;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.event.ObservesAsync;

/**
 * Session Bean implementation class SessionEJB
 */
@Stateless
@Local(EJBInterface.class)
public class SessionEJB implements EJBInterface {

    /**
     * Default constructor. 
     */
    public SessionEJB() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see EJBInterface#someMethod1(PaymentEvent)
     */
    public void someMethod1(PaymentEvent event) {
        // TODO Auto-generated method stub
    }

	/**
     * @see EJBInterface#someMethod()
     */
    public void someMethod() {
        // TODO Auto-generated method stub
    }
    
    public void nonBusinessMethod(@ObservesAsync PaymentEvent event) {
    	
    }
    
    public final void finalNonBusinessMethod(@ObservesAsync PaymentEvent event) {
    	
    }
    
    public static void staticNonBusinessMethod(@ObservesAsync PaymentEvent event) {
    	
    }

}
