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
package org.jboss.tools.jsf.jsf2.bean.model;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.jsf.jsf2.bean.model.impl.JSF2Project;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSF2ProjectFactory {
	private static final String MODEL_ID = "JSF2";
	
	public static IJSF2Project getJSF2Project(IProject project, boolean resolve) {
		JSF2Project result = null;
		KbProject kb = (KbProject)KbProjectFactory.getKbProject(project, resolve);
		
		if(kb != null) {
			result = (JSF2Project)kb.getExtensionModel(MODEL_ID);
			if(result == null) {
				result = new JSF2Project();
				result.setProject(project);
				kb.setExtensionModel(MODEL_ID, result);
			}
		}
		if(result != null && resolve) {
			result.resolve();
		}
		return result;
	}

}
