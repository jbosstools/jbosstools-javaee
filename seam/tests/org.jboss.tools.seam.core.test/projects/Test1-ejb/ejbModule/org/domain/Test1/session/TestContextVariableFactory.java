package org.domain.Test1.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;


@Name("computer")
public class TestContextVariableFactory
{
    @Logger Log log;
    
    @In Identity identity;
    
    @In String main;
    
    @In("main") boolean flag;
    
    @Factory("abc")
    int getVar(){
    	return 2;
    };
    
    @Factory
    String getCba(){
    	return "Test value is #{main.value}!";
    };
   
    public boolean calculate()
    {
        log.info("authenticating #0", identity.getUsername());
        identity.addRole("admin");
        return true;
    }
}
