package org.jboss.jsr299.tck.tests.jbt.resolution.priority;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Alternative
@ApplicationScoped
public class IllegalTableFactory {
	
	@Produces @TableQualifier(0)
	MarbleTable modelY = new MarbleTable();
	
	@Produces @TableQualifier(1)
	MarbleTable modelA = new MarbleTable();
	
	@Produces @TableQualifier(2)
	MarbleTable modelB = new MarbleTable();
}
