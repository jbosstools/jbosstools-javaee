package org.jboss.jsr299.tck.tests.jbt.validation.suppresswarnings;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("cdi-ambiguous-name")
@Named("fishDulipcatedName")
public class Fish {

	@Produces Fish fish;

	@Named("fishDulipcatedName")
	@Produces Fish fish2OK;

	@SuppressWarnings("cdi-annotated-inject")
	@Inject @Produces Fish iFishOK;

	@Inject @Produces Fish iFish2BROKEN;

	@Produces
	public void setFish(@SuppressWarnings("cdi-ambiguous-dependency") Fish fishOK) {
	}

	@SuppressWarnings({"cdi-annotated-inject", "cdi-ambiguous-dependency"})
	@Inject @Produces
	public void setFish2OK(Fish fishOK) {
	}

	@Produces
	public void setFish3(Fish fishBROKEN) {
	}

	@SuppressWarnings("all")
	@Inject Fish fish3OK;

	@SuppressWarnings("el-unresolved")
	public void useELOK() {
		String s = "#{fishDulipcatedName.abc}";
	}

	@SuppressWarnings("cdi-ambiguous-dependency")
	@Produces
	@Named("fishDulipcatedName")
	public void setFish3OK(Fish fishOK) {
	}

	@SuppressWarnings("cdi")
	@Inject Fish fish4OK;

	@Inject
	public void initializeBROKEN(@SuppressWarnings({"cdi-annotated-observer", "cdi-ambiguous-dependency"}) @javax.enterprise.event.Observes String stringOK) {
	}
}