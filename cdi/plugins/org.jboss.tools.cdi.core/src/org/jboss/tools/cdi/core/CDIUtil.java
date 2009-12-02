/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.web.kb.IKbProject;

/**
 * @author Alexey Kazakov
 */
public class CDIUtil {

	/**
	 * Adds CDI and KB builders to the project.
	 * 
	 * @param project
	 */
	public static void enableCDI(IProject project) {
		try {
			EclipseUtil.addNatureToProject(project, CDICoreNature.NATURE_ID);
			if (!project.hasNature(IKbProject.NATURE_ID)) {
				EclipseResourceUtil.addNatureToProject(project,
						IKbProject.NATURE_ID);
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * Removes CDI builder from the project.
	 * 
	 * @param project
	 */
	public static void disableCDI(IProject project) {
		try {
			EclipseUtil.removeNatureFromProject(project,
					CDICoreNature.NATURE_ID);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}
}