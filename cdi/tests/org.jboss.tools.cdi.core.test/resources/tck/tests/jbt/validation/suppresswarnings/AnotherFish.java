package org.jboss.jsr299.tck.tests.jbt.validation.suppresswarnings;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@Named("fishDulipcatedNameSecond")
public class AnotherFish { // BROKEN

	@Produces AnotherFish fish;

	@Named("fishDulipcatedNameSecond")
	@Produces Fish fish2BROKEN;

	@Inject @Produces Fish iFishBROKEN;

	@Inject @Produces Fish iFish2BROKEN;

	@Produces
	public void setFish(Fish fishBROKEN) {
	}

	@Inject @Produces
	public void setFish2OK(Fish fishBROKEN) {
	}

	@Produces
	public void setFish3(Fish fishBROKEN) {
	}

	@Inject Fish fish3BROKEN;

	public void useELBROKEN() {
		String s = "#{fishDulipcatedNameSecond.abc}";
	}

	@Produces
	@Named("fishDulipcatedNameSecond")
	public void setFish3BROLEN(Fish fishOK) {
	}
}