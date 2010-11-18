package org.domain.Test1.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;


@Name("computer2")
public class TestContextVariableOut
{
    @Logger Log log;
    
    @In Identity identity;
    
    @In String main2;
    
    @In("main2") boolean flag;
    
    @Out("aaa")
    int getVar(){
    	return 2;
    };
    
    @Out
    String getDdd(){
    	return "Test value is #{main2.value}!";
    };
   
    public boolean calculate()
    {
        log.info("authenticating #0", identity.getUsername());
        identity.addRole("admin");
        return true;
    }
}
