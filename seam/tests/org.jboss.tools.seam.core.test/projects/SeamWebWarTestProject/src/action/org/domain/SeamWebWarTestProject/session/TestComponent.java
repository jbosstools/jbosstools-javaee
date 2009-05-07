package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.*;

@Name(value="test")
public class TestComponent {
	
	String password;
	
	public Object getPart(){
		return null;
	}
	
	public boolean operate(){
		return true;
	}
	
	public String value(){
		return "Default Value";
	}

}
