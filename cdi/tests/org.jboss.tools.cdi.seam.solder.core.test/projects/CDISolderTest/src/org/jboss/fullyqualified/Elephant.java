package org.jboss.fullyqualified;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.named.Dog;
import org.jboss.seam.solder.core.FullyQualified;

@FullyQualified(Dog.class)
public class Elephant {

	@Produces
	@Named("tail")
	String getTail() {
		return "";
	}

	@Produces
	@Named("trunk")
	@FullyQualified(Dog.class)
	String getTrunk() {
		return "";
	}

	@Produces
	@Named("ear")
	String ear;

	@Produces
	@Named("eye")
	@FullyQualified(Dog.class)
	String eye;
}
