package org.domain.SeamWebWarTestProject.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;


@Name("component")
public class TestSeamComponent
{
    @Logger Log log;
    
    @In Identity identity;
    
    @In String test;
    
    @In("test") boolean flag;
    
    @Factory("test")
    int getVar(){
    	return 2;
    };
    
    @Factory
    String getTest(){
    	return "Test value is #{test.value}!";
    };
   
    public boolean authenticate()
    {
        log.info("authenticating #0", identity.getUsername());
        //write your authentication logic here,
        //return true if the authentication was
        //successful, false otherwise
        identity.addRole("admin");
        return true;
    }
}
