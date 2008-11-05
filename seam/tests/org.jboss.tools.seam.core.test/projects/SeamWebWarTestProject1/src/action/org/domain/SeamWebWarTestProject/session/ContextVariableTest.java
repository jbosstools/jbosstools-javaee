package org.domain.SeamWebWarTestProject.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Name("contextVariableTest")
@Role(name="role1")
public class ContextVariableTest {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;
    
    @Out(value="output")
    private String output;
    
    public void contextVariableTest()
    {
        //implement your business logic here
        log.info("contextVariableTest.contextVariableTest() action called");
    }
    
    @DataModel 
    private List<String> messageList=new ArrayList<String>();

    @Factory("messageList") 
    public void findMessages() {
    	
    }
    
    public List<String> getList(){
    	return messageList;
    }
    
    public String getOutput(){
    	return output;
    }
}
