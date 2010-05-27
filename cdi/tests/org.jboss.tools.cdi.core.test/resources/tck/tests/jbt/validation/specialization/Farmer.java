package org.jboss.jsr299.tck.tests.jbt.validation.specialization;

import javax.inject.Named;

@Landowner
@Named
class Farmer implements Simple {

	public String getClassName() {
		return Farmer.class.getName();
	}
}