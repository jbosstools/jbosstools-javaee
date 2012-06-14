package cdi.seam;

import javax.enterprise.inject.Produces;

import org.jboss.solder.core.Requires;

@Requires("cdi.test.Manager")
public class ManagerProducer {
	
	@Produces
	public Manager getManager() {
		return new Manager();
	}

}
