package cdi;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Alternative
public class BasicManager extends AbstractManager {

	@Produces
	public AbstractManager getManager() {
		return new AbstractManager();
	}
	
}
