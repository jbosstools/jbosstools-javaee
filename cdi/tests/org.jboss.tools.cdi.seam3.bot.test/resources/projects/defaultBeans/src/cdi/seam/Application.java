package cdi.seam;

import javax.inject.Inject;

public class Application {

	@Inject
	private Manager managerImpl;

	public Manager getManagerImpl() {
		return managerImpl;
	}

	public void setManagerImpl(Manager managerImpl) {
		this.managerImpl = managerImpl;
	}
	
}
