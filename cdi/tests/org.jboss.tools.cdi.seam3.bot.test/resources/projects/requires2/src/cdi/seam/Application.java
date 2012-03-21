package cdi.seam;

import javax.inject.Inject;

public class Application {

	@Inject
	private ManagerProducer managerProducer;

	public ManagerProducer getManagerProducer() {
		return managerProducer;
	}

	public void setManagerProducer(ManagerProducer managerProducer) {
		this.managerProducer = managerProducer;
	}
	
}
