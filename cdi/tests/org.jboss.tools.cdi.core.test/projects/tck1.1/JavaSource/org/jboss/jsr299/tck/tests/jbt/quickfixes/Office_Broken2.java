package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.inject.Inject;

public class Office_Broken2 {
	@SuppressWarnings("unused")
	@Inject @OfficeFurniture private Furniture officeFurniture;
}
