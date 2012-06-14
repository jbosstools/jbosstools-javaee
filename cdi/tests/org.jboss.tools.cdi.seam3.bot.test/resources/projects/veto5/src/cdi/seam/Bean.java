package cdi.seam;

import javax.enterprise.event.Observes;

import org.jboss.solder.core.Veto;

@Veto
public class Bean {

	public void method(@Observes @Q1 Bean event) {
		
	}
	
}
