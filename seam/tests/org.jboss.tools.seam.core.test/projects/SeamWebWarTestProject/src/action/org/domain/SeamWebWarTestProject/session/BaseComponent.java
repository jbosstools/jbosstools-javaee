package org.domain.SeamWebWarTestProject.session;

import javax.ejb.Remove;
import org.jboss.seam.annotations.Destroy;

public abstract class BaseComponent {
	
	@Remove @Destroy
	public void destroy() {}

}
