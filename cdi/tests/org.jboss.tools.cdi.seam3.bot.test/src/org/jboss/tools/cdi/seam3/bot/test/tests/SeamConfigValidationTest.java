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
import org.jboss.tools.ui.bot.ext.helper.MarkerHelper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class SeamConfigValidationTest extends Seam3TestBase {

	private static String projectName = "seamConfigValidation";
	private static final String SEAM_CONFIG = "seam-beans.xml";
	private static IMarker[] markers = null;
	
	@BeforeClass
	public static void setup() {
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		openSeamConfig();
		getAllSeamConfigMarkers();
		assertExpectedCount(markers.length, 4);
	}
	
	@Test
	public void testNonExistedField() {
		
		/* get marker by its location */
		IMarker marker = getMarkerByLocation(markers, 8);
		assertNotNull(marker);
		
		/* test the message of marker */
		assertMessageContainsExpectedValue(MarkerHelper.getMarkerMessage(marker), 
				"Cannot resolve field or method");
	}
	
	@Test
	public void testNonExistedConstructor() {
	
		/* get marker by its location */
		IMarker marker = getMarkerByLocation(markers, 11);
		assertNotNull(marker);
		
		/* test the message of marker */
		assertMessageContainsExpectedValue(MarkerHelper.getMarkerMessage(marker), 
				"Cannot resolve constructor");
		
	}

	@Test
	public void testNonSupportedParameters() {
		
		/* get marker by its location */
		IMarker marker = getMarkerByLocation(markers,15);
		assertNotNull(marker);
		
		/* test the message of marker */
		assertMessageContainsExpectedValue(MarkerHelper.getMarkerMessage(marker), 
				"Cannot resolve method");
		
	}
	
	@Test
	public void testNonExistedClass() {
		
		/* get marker by its location */
		IMarker marker = getMarkerByLocation(markers, 24);
		assertNotNull(marker);
		
		/* test the message of marker */
		assertMessageContainsExpectedValue(MarkerHelper.getMarkerMessage(marker), 
				"Cannot resolve type");
		
	}
	
	private static void getAllSeamConfigMarkers() {
		markers = getMarkersForResource(SEAM_CONFIG, projectName, 
				CDIConstants.WEBCONTENT, CDIConstants.WEB_INF);
	}

	private static void openSeamConfig() {
		packageExplorer.openFile(projectName, CDIConstants.WEBCONTENT, 
				CDIConstants.WEB_INF, SEAM_CONFIG);
		bot.cTabItem("Source").activate();
	}
	
	private static IMarker[] getMarkersForResource(String resource, String ... path) {
		MarkerHelper markerHelper = new MarkerHelper(resource, path);
		return markerHelper.getMarkers();
	}
	
	private static void assertExpectedCount(int realCount, int expectedCount) {
		String knowsIssue = "";
		if (realCount == 0 && expectedCount == 4) {
			knowsIssue = ". Known issue JBIDE-12335";
		}
		assertTrue("Expected count: " + expectedCount + " real count: " + realCount
				+ knowsIssue, realCount == expectedCount);
	}
	
	private IMarker getMarkerByLocation(IMarker[] markers, int location) {
		for (IMarker m : markers) {
			int markerLocation;
			try {
				markerLocation = Integer.parseInt(MarkerHelper.getMarkerLineNumber(m));
			} catch (NumberFormatException nfe) {
				return null;
			}
			if (markerLocation == location) {
				return m;
			}
		}
		return null;
	}
	
	private void assertMessageContainsExpectedValue(String message,
			String... expectedValues) {
		for (String value : expectedValues) {
			assertContains(value, message);
		}
	}

}
