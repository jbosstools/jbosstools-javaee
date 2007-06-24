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
package org.jboss.tools.jsf.model.handlers.bean;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintQClassName;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;

public class ClassExistsCheck {
	XAttributeConstraintQClassName constraint = new XAttributeConstraintQClassName();
	protected XModelObject context;
	protected String qualifiedClassName = "";
	protected IType existingClass = null;
	protected IJavaProject javaProject = null;
	boolean valid = false;

	public void setModelContext(XModelObject context) {
		this.context = context;
		try {
			IProject project = EclipseResourceUtil.getResource(context).getProject();
			javaProject = EclipseResourceUtil.getJavaProject(project);
		} catch (Exception e) {
			JSFModelPlugin.log(e);
		}
		qualifiedClassName = "";
		existingClass = null;
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public boolean classExists() {
		return existingClass != null;
	}
	
	public IType getExistingClass() {
		return existingClass;
	}

	public void update(String classname) {
		if(classname.equals(qualifiedClassName)) return;
		qualifiedClassName = classname;
		valid = classname.length() > 0 && !classname.endsWith(".") && constraint.accepts(classname);
		if(!valid) {
			existingClass = null;
			return;
		}
		try {
			if(javaProject != null) existingClass = javaProject.findType(qualifiedClassName);
		} catch (Exception e) {
			existingClass = null;
			JSFModelPlugin.log(e);
		}			
	}

}
