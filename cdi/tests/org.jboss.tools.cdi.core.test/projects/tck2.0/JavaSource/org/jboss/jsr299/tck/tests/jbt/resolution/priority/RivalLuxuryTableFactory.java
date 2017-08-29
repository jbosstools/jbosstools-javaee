package org.jboss.jsr299.tck.tests.jbt.resolution.priority;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

@Alternative
@ApplicationScoped
@Priority(APPLICATION + 100)
public class RivalLuxuryTableFactory {
	
	@Produces @TableQualifier(2)
	MarbleTable modelB = new MarbleTable();
}
