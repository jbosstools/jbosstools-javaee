package org.domain.Test1.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;


@Name("computer3")
public class TestContextVariableDataModel
{
    @Logger Log log;
    
    @In Identity identity;
    
    @In String main3;
    
    @In("main3") boolean flag;
    
    @DataModel("data")
    int getVar(){
    	return 2;
    };
    
    @DataModel
    String getModel(){
    	return "Test value is #{main3.value}!";
    };
   
    public boolean calculate()
    {
        log.info("authenticating #0", identity.getUsername());
        identity.addRole("admin");
        return true;
    }
}
