package org.jboss.defaultbean;

import javax.inject.Inject;

public class Town {

	@Inject
	@Small
	Home small;
	
	@Inject
	@Big
	Home big;
	
	@Inject
	@Huge
	Home huge;

	@Inject
	@Cozy
	Home cozy;

	@Inject
	Home ruins;

}
