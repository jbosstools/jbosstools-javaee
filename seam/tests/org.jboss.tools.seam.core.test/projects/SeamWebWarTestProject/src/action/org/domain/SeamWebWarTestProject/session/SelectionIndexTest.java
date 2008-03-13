package org.domain.SeamWebWarTestProject.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Name("selectionIndexTest")
public class SelectionIndexTest {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;

    @DataModel 
    private List<String> messageList=new ArrayList<String>();

    @DataModel 
    private List<String> nameList=new ArrayList<String>();
    
    @DataModelSelectionIndex("messageList") int index;
    
    public List<String> getList(){
    	return messageList;
    }

    public List<String> getNames(){
    	return nameList;
    }
    
    public void selectionIndexTest()
    {
        //implement your business logic here
        log.info("selectionIndexTest.selectionIndexTest() action called");
    }
}
