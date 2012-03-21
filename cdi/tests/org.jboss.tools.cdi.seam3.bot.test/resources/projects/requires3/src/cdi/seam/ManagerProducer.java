package cdi.seam;

import javax.enterprise.inject.Produces;

import org.jboss.seam.solder.core.Requires;

@Requires("cdi.test.Manager")
public class ManagerProducer {

	@Produces @Q1 
	public ManagerProducer getManagerProducer() {
		return new ManagerProducer();
	}
	
}
