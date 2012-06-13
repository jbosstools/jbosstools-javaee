/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam3.bot.test.tests;

import org.eclipse.core.resources.IMarker;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.base.Seam3TestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.MarkerHelper;
import org.junit.After;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class InterfaceAndAbstractValidationTest extends Seam3TestBase {

	@After
	public void cleanWS() {
		projectExplorer.deleteAllProjects();
	}
	
	@Test
	public void testInterfaceValidation() {
		
		/* import test project */
		String projectName = "interface1";
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		bot.sleep(Timing.time3S()); // necessary to CDI Validation computation
		
		/* get markers for beans.xml */
		IMarker[] markers = getMarkersForResource(CDIConstants.BEANS_XML, projectName, 
				CDIConstants.WEBCONTENT, CDIConstants.WEB_INF);
		
		/* assert expected count */
		assertExpectedCount(markers.length ,1);
		
		/* assert message contains expected value */
		assertMessageContainsExpectedValue(MarkerHelper.getMarkerMessage(markers[0]), 
				"Interface ", "cannot be configured as a bean");
		
	}
	
	@Test
	public void testAbstractTypeValidation() {
		
		/* import test project */
		String projectName = "abstract1";
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		bot.sleep(Timing.time3S()); // necessary to CDI Validation computation
		
		/* get markers for beans.xml */
		IMarker[] markers = getMarkersForResource(CDIConstants.BEANS_XML, projectName, 
				CDIConstants.WEBCONTENT, CDIConstants.WEB_INF);
		
		/* assert expected count */
		assertExpectedCount(markers.length ,1);
		
		/* assert message contains expected value */
		assertMessageContainsExpectedValue(MarkerHelper.getMarkerMessage(markers[0]), 
				"Abstract type", "cannot be configured as a bean");
		
	}
	
	private void assertMessageContainsExpectedValue(String message,
			String... expectedValues) {
		for (String value : expectedValues) {
			assertContains(value, message);
		}
	}
	
	private IMarker[] getMarkersForResource(String resource, String ... path) {
		MarkerHelper markerHelper = new MarkerHelper(resource, path);
		return markerHelper.getMarkers();
	}
	
	private void assertExpectedCount(int realCount, int expectedCount) {
		assertTrue("Expected count: " + expectedCount + " real count: " + realCount, 
				realCount == expectedCount);
	}
	
}
