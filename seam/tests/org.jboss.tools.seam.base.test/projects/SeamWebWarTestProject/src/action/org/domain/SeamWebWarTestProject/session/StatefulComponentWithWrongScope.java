package org.domain.SeamWebWarTestProject.session;;

import javax.ejb.Stateful;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import javax.ejb.Remove;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.ScopeType;

@Scope(ScopeType.PAGE)
@Stateful
@Name("StatefulComponentWithWrongScope")
public class StatefulComponentWithWrongScope {

	@Remove
	public void remove() {
	}

	@Destroy
	public void destroy() {
	}

}
