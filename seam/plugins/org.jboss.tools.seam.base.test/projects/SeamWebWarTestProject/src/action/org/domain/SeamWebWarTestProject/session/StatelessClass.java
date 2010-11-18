package org.domain.SeamWebWarTestProject.session;
import javax.ejb.Stateless;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
@Stateless @Name("StatelessClass")
public class StatelessClass {

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

	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}
    
}