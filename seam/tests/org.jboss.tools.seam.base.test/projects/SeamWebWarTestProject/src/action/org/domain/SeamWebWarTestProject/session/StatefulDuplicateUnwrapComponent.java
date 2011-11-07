package org.domain.SeamWebWarTestProject.session;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("StatefulDuplicateUnwrapComponent")

@Stateful
@Scope(ScopeType.APPLICATION)
public class StatefulDuplicateUnwrapComponent {

	private String abc;
	
    @Logger private Log log;
	
    
    //seam-gen method
    public String statefulComponent()
    {
        //implement your business logic here
        log.info("statefulComponent.statefulComponent() action called");
        return "success";
    }
	
   //add additional action methods
    @Destroy
	public void destroyMethod(){
		
	}
	
    @Unwrap
    public void unwrapMethod(){
		
	}

    @Unwrap
    public void unwrapMethod2(){
		
	}
    
    @Remove
    public void removeMethod(){
    	
    }

	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}
    
}