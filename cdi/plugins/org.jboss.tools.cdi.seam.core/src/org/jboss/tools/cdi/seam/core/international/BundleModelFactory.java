/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;

/**
 * 
 * @author Viacheslav Kabanlvich
 *
 */
public class BundleModelFactory {

	/**
	 * 
	 * @param project
	 * @return bundle model for a project with enabled cdi capabilities and with seam international module in classpath
	 */
	public static IBundleModel getBundleModel(IProject project) {
		CDICoreNature cdi = CDICorePlugin.getCDI(project, true);
		if(cdi != null) {
			CDISeamInternationalExtension extension = CDISeamInternationalExtension.getExtension(cdi);
			if(extension != null) {
				return extension.getBundleModel();
			}
		}
		return null;
	}

}
