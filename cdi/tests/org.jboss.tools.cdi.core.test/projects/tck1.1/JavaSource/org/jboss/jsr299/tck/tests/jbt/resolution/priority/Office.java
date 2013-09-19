package org.jboss.jsr299.tck.tests.jbt.resolution.priority;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Office {

	/**
	 * Eligible beans with priorities 2000, 2001, 2100
	 * Resolved to bean with maximum priority.
	 */
	@Inject MarbleTable marbleTable;

	/**
	 * Assignable non-eligible bean without priority.
	 * Eligible bean with priority.
	 * Resolved.
	 */
	@Inject @TableQualifier(0) MarbleTable marbleTableY;

	/**
	 * Eligible beans with priorities 2001, 2100
	 * Resolved to bean with maximum priority.
	 */
	@Inject @TableQualifier(1) MarbleTable marbleTableA;  

	/**
	 * Eligible beans with priorities 2001, 2100, 2100
	 * Ambiguous dependency.
	 */
	@Inject @TableQualifier(2) MarbleTable marbleTableB;  
}
