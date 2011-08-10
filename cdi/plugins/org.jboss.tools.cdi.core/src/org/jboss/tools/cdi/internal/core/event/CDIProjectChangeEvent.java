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
package org.jboss.tools.cdi.internal.core.event;

import java.util.EventObject;

import org.jboss.tools.cdi.core.ICDIProject;

/**
 * CDI Project change event object
 * 
 * @author Victor V. Rubezhny
 */
public class CDIProjectChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	/**
	 * Modified CDI project. 
	 */
	ICDIProject project;
	
	public CDIProjectChangeEvent(ICDIProject project) {
		super(project);
		this.project = project;
	}
	
	/**
	 * Returns modified CDI project
	 * @return
	 */	
	public ICDIProject getProject() {
		return project;
	}
}
