package org.jboss.tools.seam.core.test;

import org.jboss.tools.tests.AbstractPluginsLoadTest;

import junit.framework.TestCase;

public class SeamPluginsLoadTest extends AbstractPluginsLoadTest {
	
	public void testBundlesAreLoadedForSeamFeature(){
		testBundlesAreLoadedFor("org.jboss.tools.seam.feature");
	}
}
