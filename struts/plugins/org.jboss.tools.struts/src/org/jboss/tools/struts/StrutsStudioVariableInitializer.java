/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;

import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.jst.web.WebModelPlugin;

public class StrutsStudioVariableInitializer extends ClasspathVariableInitializer {
	public static final String STRUTSSTUDIO_LIB_HOME = "STRUTSSTUDIO_LIB_HOME";
	 
	public void initialize(String variable) 
	{
		if (STRUTSSTUDIO_LIB_HOME.equals(variable) /*&& JavaCore.getClasspathVariable(variable) == null*/)
		{
			ClassLoaderUtil.init();
			IPath ssLibPath = null;
			IPreferenceStore store = StrutsModelPlugin.getDefault().getPreferenceStore();
			if (store.contains(STRUTSSTUDIO_LIB_HOME) && !store.isDefault(STRUTSSTUDIO_LIB_HOME)) {
				String value = store.getString(STRUTSSTUDIO_LIB_HOME);
				ssLibPath = new Path(value);
				if (!ssLibPath.toFile().isDirectory()) ssLibPath = null;  
			} 
			
			if (ssLibPath == null) {
				IPath pluginPath = WebModelPlugin.getTemplateStatePath();
//					new Path(EclipseResourceUtil.getInstallPath(Platform.getBundle("org.jboss.tools.common.projecttemplates")));
				//ssLibPath = pluginPath.removeLastSegments(3).append("lib");
				ssLibPath = pluginPath.append("lib");
			}
			
			try	{
				JavaCore.setClasspathVariable(variable, ssLibPath, new NullProgressMonitor());
			} catch (JavaModelException ex) {
                StrutsModelPlugin.getPluginLog().logError(ex);
			}
		}
	}
	
	public static void save(IPreferenceStore store)
	{
		IPath value = JavaCore.getClasspathVariable(STRUTSSTUDIO_LIB_HOME);
		if (value != null) store.setValue(STRUTSSTUDIO_LIB_HOME, value.toString()); 	
	}
}
