/*************************************************************************************
 * Copyright (c) 2008-2009 JBoss by Red Hat and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.cdi.ui.test.marker;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.cdi.core.test.tck.validation.ValidationTest;
import org.jboss.tools.cdi.ui.marker.AddLocalBeanMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldStaticMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodBusinessMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodPublicMarkerResolution;

/**
 * @author Daniel Azarov
 * 
 */
public class CDIMarkerResolutionTest  extends ValidationTest {
	public static final String MARKER_TYPE = "org.jboss.tools.cdi.core.cdiproblem";

	public void testMakeFieldStaticResolution() throws CoreException {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerOfSessionBeanBroken.java");
		
		assertTrue("File - "+file.getFullPath()+" must be exist",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof MakeFieldStaticMarkerResolution) {
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("Quick fix: \"Make field static\" doesn't exist.", found);
	}
	
	public void testMakeMethodBusinessResolution() throws CoreException {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer.java");
		
		assertTrue("File - "+file.getFullPath()+" must be exist",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found1 = false;
		boolean found2 = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof MakeMethodBusinessMarkerResolution) {
					found1 = true;
				}
				if (resolution instanceof AddLocalBeanMarkerResolution) {
					found2 = true;
				}
			}
		}
		assertTrue("Quick fix: \"Make method business\" doesn't exist.", found1);
		assertTrue("Quick fix: \"Add @LocalBean annotation\" doesn't exist.", found2);
	}

	public void testMakeMethodPublicResolution() throws CoreException {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerNoInterface.java");
		
		assertTrue("File - "+file.getFullPath()+" must be exist",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof MakeMethodPublicMarkerResolution) {
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("Quick fix: \"Make method public\" doesn't exist.", found);
	}

}
