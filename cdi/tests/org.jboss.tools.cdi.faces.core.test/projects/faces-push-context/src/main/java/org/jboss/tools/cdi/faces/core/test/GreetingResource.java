package org.jboss.tools.cdi.faces.core.test;

import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {
	@Inject
	@Push
	private PushContext pushContext;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}