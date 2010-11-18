package org.domain.SeamWebWarTestProject.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Name("component12")
public class Component12 {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;
    
    @DataModel 
    private List<String> messageList=new ArrayList<String>();
    
    @Factory("messageList12") 
    public List<String>  findMessages() {
    	return messageList;
    }
    
    public List<String> getList(){
    	return messageList;
    }

    
    public void component12()
    {
        //implement your business logic here
        log.info("component12.component12() action called");
        //facesMessages.add("component12");
    }
}
