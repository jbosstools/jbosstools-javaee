 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author Alexey Kazakov
 */
public abstract class SeamProjectChange extends Change {

	protected IProject project;

	public final static String[] PROJECT_NAME_PROPERTIES = {
		ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT,
		ISeamFacetDataModelProperties.SEAM_EAR_PROJECT,
		ISeamFacetDataModelProperties.SEAM_EJB_PROJECT,
		ISeamFacetDataModelProperties.SEAM_TEST_PROJECT
	};

	public final static String[] FOLDER_PROPERTIES = {
		ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER
	};

	/**
	 * @param project Project that we are asked to check and update.
	 */
	public SeamProjectChange(IProject project) {
		this.project = project;
	}

	/**
	 * @return false if we is not going to update this project
	 */
	abstract public boolean isRelevant();

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
	 */
	@Override
	public Object getModifiedElement() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	@Override
	public String getName() {
		return "Update Seam Project Properties for " + project.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initializeValidationData(IProgressMonitor pm) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,	OperationCanceledException {
		return new RefactoringStatus();
	}

	/**
	 * @return preferences of project 
	 */
	protected IEclipsePreferences getSeamPreferences() {
		IScopeContext projectScope = new ProjectScope(project);
		return projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
	}
	
	protected HashMap<String, String> preferences = new HashMap<String, String>();
	
	/**
	 * for test purpose
	 * @return
	 */
	public HashMap<String, String> getPreferencesForTest(){
		return preferences;
	}
	
	
}