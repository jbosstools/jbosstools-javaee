package org.jboss.exact;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.solder.core.Exact;

public class FishFactory {
	@Inject
	@Exact(Salmon.class)
	IFish peacefulFish;

	@Inject
	@Exact(Shark.class)
	IFish dangerousFish;

	@Produces
	public IFish getTastyFish(@Exact(Salmon.class) IFish fish) {
		return fish;
	}
}
