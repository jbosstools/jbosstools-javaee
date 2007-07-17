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
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetFilterSetFactory;
import org.jboss.tools.seam.ui.widget.editor.INamedElement;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author eskimo
 *
 */
public abstract class SeamBaseOperation extends AbstractOperation {

	/**
	 * @param label
	 */
	public SeamBaseOperation(String label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		Map<String, INamedElement> params = (Map)info.getAdapter(Map.class);
		Map<String, Object> vars = new HashMap<String, Object>();
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamFacetPreferences(
				ResourcesPlugin.getWorkspace().getRoot().getProject(params.get(IParameter.SEAM_PROJECT_NAME).getValueAsString()));
		try {
			
			for (String key : seamFacetPrefs.keys()) {
				vars.put(key, seamFacetPrefs.get(key, ""));
			}
			
			for (Object valueHolder : params.values()) {
				INamedElement elem  = (INamedElement)valueHolder;
				vars.put(elem.getName(),elem.getValue().toString());
			}
			vars.put(ISeamFacetDataModelProperties.SEAM_PROJECT_INSTANCE,
					ResourcesPlugin.getWorkspace().getRoot().getProject(
							vars.get(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME).toString()));
			
			return execute(monitor, vars);
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return Status.OK_STATUS;
	}

	/**
	 * 
	 * @param monitor
	 * @param params
	 * @return
	 * @throws ExecutionException
	 */
	public IStatus execute(IProgressMonitor monitor, Map<String,Object> vars) 
		throws ExecutionException {
		
		// Target Project 
		IProject targetProject = (IProject)vars.get(ISeamFacetDataModelProperties.SEAM_PROJECT_INSTANCE);
		
		IVirtualComponent com = ComponentCore.createComponent(targetProject);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		IContainer webRootContainer = webRootFolder.getUnderlyingFolder();
		
		FilterSetCollection filters = new FilterSetCollection(SeamFacetFilterSetFactory.createFiltersFilterSet(vars));
		
		// Input data
//		String beanName = vars.get(IParameter.SEAM_BEAN_NAME).toString();
		String actionFolder = vars.get(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString();
		String interfaceName = vars.get(IParameter.SEAM_LOCAL_INTERFACE_NAME).toString();
		String testFolder = vars.get(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME).toString();
		String pageName = vars.get(IParameter.SEAM_PAGE_NAME).toString();
		
		File seamTargetInterfaceFile = new File(targetProject.getLocation().toFile().toString() 
				+ "/src/" + actionFolder.replace('.','/') + "/" + interfaceName + ".java");
		AntCopyUtils.copyFileToFile(getBeanFile(vars),seamTargetInterfaceFile,filters,true); 
		
		File seamTargetActionTestFile = new File(targetProject.getLocation().toFile().toString() 
				+ "/src/" + testFolder.replace('.','/') + "/" + interfaceName + "Test.java");
		AntCopyUtils.copyFileToFile(getTestClassFile(vars),seamTargetActionTestFile,filters,true); 			
	
		File seamTargetTestinFile = new File(targetProject.getLocation().toFile().toString() 
				+ "/src/" + testFolder.replace('.','/') + "/" + interfaceName + "Test.xml");
		AntCopyUtils.copyFileToFile(getTestngXmlFile(vars),seamTargetTestinFile,filters,true); 			
	
		File seamTargetActionPageFile = new File(webRootContainer.getLocation().toFile().toString() 
				+ "/" + pageName + ".xhtml");
		AntCopyUtils.copyFileToFile(getPageXhtml(vars),seamTargetActionPageFile,filters,true); 	
		
		try {
			targetProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			}
		return Status.OK_STATUS;
	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return Status.OK_STATUS;
	}

	@Override
	public boolean canRedo() {
		return false;
	}

	@Override
	public boolean canUndo() {
		return false;
	}
	
	public File getSeamFolder(Map<String, Object> vars) {
		return new File(vars.get(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString(),"seam-gen");		
	}

	public abstract File getBeanFile(Map<String, Object> vars);
	
	public abstract File getTestClassFile(Map<String, Object> vars);
	
	public abstract File getTestngXmlFile(Map<String, Object> vars);
	
	public abstract File getPageXhtml(Map<String, Object> vars);
}
