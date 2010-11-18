package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.Observer;

public class NonComponentWithObserverMethod {

	private String abc;
	
	@Observer
    public void observerMethod(){
		
	}
	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}
}