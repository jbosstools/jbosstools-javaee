package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.Create;

public class NonComponentWithCreateMethod {

	private String abc;
	
	@Create
    public void createMethod(){
		
	}
	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}
}