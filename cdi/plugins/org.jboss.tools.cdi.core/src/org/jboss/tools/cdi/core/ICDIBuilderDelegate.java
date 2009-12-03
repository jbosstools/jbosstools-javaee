/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
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
import org.jboss.tools.cdi.internal.core.scanner.FileSet;

/**
 * Builder delegate performs build for specific kind of cdi project.
 * CDICoreBuilder collects builder delegates registered by extension point
 * and resolves which of them is best suited for the project instance.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface ICDIBuilderDelegate {

	public String getID();

	public int computeRelevance(IProject project);

	public Class<? extends ICDIProject> getProjectImplementationClass();

	public void build(FileSet fileSet, CDICoreNature projectNature);
	
}
