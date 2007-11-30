package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

@Name("abcComponent")
public class AbcComponent {
	
    @Logger private Log log;
    
    public void abcComponent()
    {
        //implement your business logic here
        log.info("abcComponent.abcComponent() action called");
    }
	
   //add additional action methods
	
}
