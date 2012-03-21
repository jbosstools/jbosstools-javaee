package cdi.seam;

import javax.enterprise.inject.Produces;
import org.jboss.seam.solder.core.Veto;

@Veto
public class Bean {

	@Produces @Q1 private Manager manager = new Manager();

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
}
