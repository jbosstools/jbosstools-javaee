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
package org.jboss.tools.cdi.core.extension;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.util.EclipseJavaUtil;

/**
 * Default implementation just checks that class represented by 'runtime'
 * exists in the classpath of Java project.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DefaultExtensionRecognizer implements IExtensionRecognizer {

	@Override
	public boolean containsExtension(String runtime, IJavaProject javaProject) {
		try {
			IType type = EclipseJavaUtil.findType(javaProject, runtime);
			return type != null && type.exists();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
			return false;
		}
	}
}
