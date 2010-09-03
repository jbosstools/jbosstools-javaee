package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.Unwrap;

public class NonComponentWithUnwrapMethod {

	private String abc;
	
	@Unwrap
    public void unwrapMethod(){
		
	}
	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}
}