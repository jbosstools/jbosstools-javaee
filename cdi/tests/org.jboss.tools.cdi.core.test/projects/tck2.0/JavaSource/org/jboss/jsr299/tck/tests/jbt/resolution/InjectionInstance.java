package org.jboss.jsr299.tck.tests.jbt.resolution;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class InjectionInstance {

	@Inject Instance<String> multipleOk;
	@Inject Instance<String> emptyOk;

	@Inject String multipleBroken;
	@Inject StringBuffer emptyBroken;
}