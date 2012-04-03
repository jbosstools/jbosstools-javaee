package cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;

public class RemoveCodeBean {

	public void method(@Disposes String param1, @Observes String param2) {
		
	}
	
}
