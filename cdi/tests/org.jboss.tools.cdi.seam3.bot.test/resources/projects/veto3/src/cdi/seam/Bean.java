package cdi.seam;

import javax.enterprise.inject.Produces;

import org.jboss.solder.core.Veto;

@Veto
public class Bean {
	
	@Produces @Q1
	public Manager getManager() {
		return new Manager();
	}

}
