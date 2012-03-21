package cdi.seam;

import javax.inject.Inject;

public class Application {

	@Inject @Q1
	private Manager manager;

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
}
