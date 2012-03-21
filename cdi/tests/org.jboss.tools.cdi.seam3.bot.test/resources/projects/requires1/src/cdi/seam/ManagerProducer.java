package cdi.seam;

import javax.enterprise.inject.Produces;

import org.jboss.seam.solder.core.Requires;

@Requires("cdi.test.Manager")
public class ManagerProducer {
	
	@Produces
	public Manager getManager() {
		return new Manager();
	}

}
