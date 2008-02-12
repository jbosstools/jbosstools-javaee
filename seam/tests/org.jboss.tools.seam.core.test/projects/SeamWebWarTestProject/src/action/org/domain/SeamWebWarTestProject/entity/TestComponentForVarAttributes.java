package org.domain.SeamWebWarTestProject.entity;

import java.io.Serializable;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import java.util.List;

@Scope(ScopeType.EVENT)
@Name("testComponentForVars")
public class TestComponentForVarAttributes implements Serializable {
	static final long serialVersionUID = 1001;

	public static class Value {
		public String getName() {
			return null;
		}
	}

	public List<Value> getList() {
	    return null;
	}
}