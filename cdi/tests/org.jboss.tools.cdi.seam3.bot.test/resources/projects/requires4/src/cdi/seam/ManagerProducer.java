package cdi.seam;

import javax.enterprise.inject.Produces;

import org.jboss.seam.solder.core.Requires;

@Requires("cdi.test.Manager")
public class ManagerProducer {

	@Produces @Q1 private ManagerProducer managerProducer = new ManagerProducer();

	public ManagerProducer getManagerProducer() {
		return managerProducer;
	}

	public void setManagerProducer(ManagerProducer managerProducer) {
		this.managerProducer = managerProducer;
	}
	
}
