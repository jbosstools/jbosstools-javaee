package cdi.seam;

import javax.enterprise.event.Observes;

import org.jboss.seam.solder.core.Requires;

@Requires("cdi.test.Manager")
public class ManagerProducer {

	public void method(@Observes @Q1 ManagerProducer event) {
		
	}
	
}
