package org.jboss.jsr299.tck.tests.jbt.resolution.priority;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

@Alternative
@ApplicationScoped
@Priority(APPLICATION + 1)
public class TableFactory {
	
	@Produces MarbleTable modelX = new MarbleTable();
	
	@Produces @TableQualifier(0)
	MarbleTable modelY = new MarbleTable();

	@Produces @TableQualifier(1)
	MarbleTable modelA = new MarbleTable();
	
	@Produces @TableQualifier(2)
	MarbleTable modelB = new MarbleTable();
}
