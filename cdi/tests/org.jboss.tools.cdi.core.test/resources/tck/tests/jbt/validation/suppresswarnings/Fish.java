package org.jboss.jsr299.tck.tests.jbt.validation.suppresswarnings;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("org.jboss.tools.cdi.core.validator.problem.ambiguousElNames")
@Named("fishDulipcatedName")
public class Fish {

	@Produces Fish fish;

	@Named("fishDulipcatedName")
	@Produces Fish fish2OK;

	@SuppressWarnings("org.jboss.tools.cdi.core.validator.problem.producerAnnotatedInject")
	@Inject @Produces Fish iFishOK;

	@Inject @Produces Fish iFish2BROKEN;

	@Produces
	public void setFish(@SuppressWarnings("org.jboss.tools.cdi.core.validator.problem.unsatisfiedInjectionPoints") Fish fishOK) {
	}

	@SuppressWarnings({"org.jboss.tools.cdi.core.validator.problem.producerAnnotatedInject", "org.jboss.tools.cdi.core.validator.problem.unsatisfiedInjectionPoints"})
	@Inject @Produces
	public void setFish2OK(Fish fishOK) {
	}

	@Produces
	public void setFish3(Fish fishBROKEN) {
	}

	@SuppressWarnings("unsatisfiedInjectionPoints")
	@Inject Fish fish3OK;

	@SuppressWarnings("unknownElVariablePropertyName")
	public void useEL() {
		String s = "#{fishDulipcatedName.abc}";
	}
}