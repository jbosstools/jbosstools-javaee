package org.jboss.named;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.fullyqualified.Elephant;
import org.jboss.seam.solder.core.FullyQualified;

public class Dog {

	@Produces
	@Named("hair")
	String getHair() {
		return "";
	}

	@Produces
	@Named("nose")
	@FullyQualified(Elephant.class)
	String getNose() {
		return "";
	}

	@Produces
	@Named("jaws")
	String jaws;

	@Produces
	@Named("black-eye")
	@FullyQualified(Elephant.class)
	String eye;
}
