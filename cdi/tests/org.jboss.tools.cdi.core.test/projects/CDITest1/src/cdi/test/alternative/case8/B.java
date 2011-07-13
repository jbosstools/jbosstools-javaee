package cdi.test.alternative.case8;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Alternative
public class B extends A {
	@Produces
	public P p() {
		return new P("");
	}
}
