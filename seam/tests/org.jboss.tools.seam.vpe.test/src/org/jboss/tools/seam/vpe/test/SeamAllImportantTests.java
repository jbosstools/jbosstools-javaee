package org.jboss.tools.seam.vpe.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite containing all important Seam tests
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	JBIDE1484Test.class,
	OpenOnForDecorateTest.class
})
public class SeamAllImportantTests {
}
