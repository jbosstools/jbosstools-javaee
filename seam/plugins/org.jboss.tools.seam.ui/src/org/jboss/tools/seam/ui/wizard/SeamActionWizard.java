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
package org.jboss.tools.seam.ui.wizard;

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.INewWizard;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetFilterSetFactory;

/**
 * 
 * @author eskimo
 *
 */
public class SeamActionWizard extends SeamBaseWizard implements INewWizard {

	/**
	 * 
	 */
	public SeamActionWizard() {
		super(CREATE_SEAM_ACTION);
		setWindowTitle("New Seam Action");
		//setDefaultPageImageDescriptor();
		addPage(new SeamActionWizardPage1());
	}

	/**
	 * 
	 * TODO move operations to core plugin
	 */
	public static final IUndoableOperation CREATE_SEAM_ACTION = new SeamBaseOperation("Action creating operation"){
		
		
		public File getBeanFile(Map<String, Object> vars)  {
			return new File(getSeamFolder(vars),"src/ActionJavaBean.java");
		}
		
		public File getTestClassFile(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"test/ActionTest.java");
		}
		
		public File getTestngXmlFile(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"test/testng.xml");
		}
		
		public File getPageXhtml(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"view/action.xhtml");
		}
	};
}
