/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import java.text.MessageFormat;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;

public class MarkerAssertUtil extends Assert {
	public static void assertMarkerIsCreatedForLine(IProject project, String fileName, String template, Object[] parameters, int lineNumber) throws CoreException{
		assertMarkerIsCreatedForLine(project, fileName, template, parameters, lineNumber, true);
	}

	public static void assertMarkerIsCreatedForLine(IProject project, String fileName, String template, Object[] parameters, int lineNumber, boolean validate) throws CoreException{
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);

		if(validate) {
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
		}

		IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
		for (int i = 0; i < markers.length; i++) {
			String message = markers[i].getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
			int line = markers[i].getAttribute(IMarker.LINE_NUMBER, -1); //$NON-NLS-1$
			if(message.equals(messagePattern) && line == lineNumber)
				return;
		}
		fail("Marker "+messagePattern+" for line - "+lineNumber+" not found");
	}

	public static void assertMarkerIsNotCreatedForLine(IProject project, String fileName, String template, Object[] parameters, int lineNumber) throws CoreException{
		assertMarkerIsNotCreatedForLine(project, fileName, template, parameters, lineNumber, true);
	}

	public static void assertMarkerIsNotCreatedForLine(IProject project, String fileName, String template, Object[] parameters, int lineNumber, boolean validate) throws CoreException{
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);

		if(validate) {
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
		}

		IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
		for (int i = 0; i < markers.length; i++) {
			String message = markers[i].getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
			int line = markers[i].getAttribute(IMarker.LINE_NUMBER, -1); //$NON-NLS-1$
			if(message.equals(messagePattern) && line == lineNumber){
				fail("Marker "+messagePattern+" for line - "+lineNumber+" has been found");
			}
		}
	}
}
