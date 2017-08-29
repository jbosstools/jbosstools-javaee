package org.jboss.jsr299.tck.tests.jbt.vetoed;

import javax.inject.Inject;
import org.jboss.jsr299.tck.tests.jbt.vetoed.seas.Black;
import org.jboss.jsr299.tck.tests.jbt.vetoed.seas.inner.Caspian;

public class Injections {

	@Inject Pond pond; //vetoed - not a bean
	@Inject Pond.Creek creek; //not vetoed - a bean
	@Inject Pond.Spring spring; //vetoed - not a bean

	@Inject Black black; //in vetoed package - not a bean
	
	@Inject Caspian caspian; //in subpackage of vetoed package - a bean
}