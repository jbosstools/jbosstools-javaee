package org.domain.SeamWebWarTestProject.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Name("selectionTest")
public class SelectionTest {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;
    
    @DataModel 
    private List<String> messageList=new ArrayList<String>();

    @DataModel 
    private List<String> nameList=new ArrayList<String>();
    
    @DataModelSelection("messageList") String s;
    
    public List<String> getList(){
    	return messageList;
    }

    public List<String> getNames(){
    	return nameList;
    }
    
    public void selectionTest()
    {
        //implement your business logic here
        log.info("selectionTest.selectionTest() action called");
    }
}
