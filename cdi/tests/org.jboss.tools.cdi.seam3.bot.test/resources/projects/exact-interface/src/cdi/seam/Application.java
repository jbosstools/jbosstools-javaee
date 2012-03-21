package cdi.seam;

import javax.inject.Inject;

import org.jboss.seam.solder.core.Exact;

public class Application {

	@Inject		
	@Exact(Manager.class)
	private Manager manager;

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
}
