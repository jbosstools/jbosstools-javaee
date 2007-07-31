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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.FilterSet;
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
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				params.get(IParameter.SEAM_PROJECT_NAME).getValueAsString());
		
		Map<String, Object> vars = new HashMap<String, Object>();
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamFacetPreferences(project);
		
		try {
			
			for (String key : seamFacetPrefs.keys()) {
				vars.put(key, seamFacetPrefs.get(key, ""));
			}
			
			for (Object valueHolder : params.values()) {
				INamedElement elem  = (INamedElement)valueHolder;
				vars.put(elem.getName(),elem.getValue().toString());
			}
			
			String actionFolder = vars.get(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString();
			String testFolder = vars.get(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME).toString();
			
			IVirtualComponent com = ComponentCore.createComponent(project);
			IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
			IContainer webRootContainer = webRootFolder.getUnderlyingFolder();
			
			vars.put(ISeamFacetDataModelProperties.SEAM_PROJECT_INSTANCE,project);
			vars.put(IParameter.SEAM_PROJECT_LOCATION_PATH,project.getLocation().toFile().toString());
			vars.put(IParameter.SEAM_PROJECT_WEBCONTENT_PATH,webRootContainer.getLocation().toFile().toString());
			vars.put(IParameter.SEAM_EAR_PROJECT_LOCATION_PATH,project.getLocation().removeLastSegments(1).append(project.getName()+"-ejb").toFile().toString());
			vars.put(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH, actionFolder.replace('.','/'));
			vars.put(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME, actionFolder);
			vars.put(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH, testFolder.replace('.','/'));			
			vars.put(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, testFolder);
			List<String[]> fileMapping = getFileMappings(vars);	
			List<String[]> fileMappingCopy = applayVariables(fileMapping,vars);
			FilterSetCollection filters = getFilterSetCollection(vars);
			for (String[] mapping : fileMappingCopy) {
				if(SeamCorePlugin.getDefault().isDebugging()) {
					System.out.println(mapping[0] + "->" + mapping[1]);
				}
				AntCopyUtils.copyFileToFile(new File(mapping[0]),new File(mapping[1]),filters,true); 			
			}
			
			project.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return Status.OK_STATUS;
	}

	/**
	 * @param fileMapping
	 * @param vars
	 * @return
	 */
	public static List<String[]> applayVariables(List<String[]> fileMapping,
			Map<String, Object> vars) {
		List<String[]> result = new ArrayList<String[]>();
		for (String[] filter : fileMapping) {
			String source = filter[0];
			for (Object property : vars.keySet()){
				if(source.contains("${"+property.toString()+"}")) {
					source = source.replace("${"+property.toString()+"}",vars.get(property.toString()).toString());
				}
			}
			String dest = filter[1];
			for (Object property : vars.keySet()){
				if(dest.contains("${"+property.toString()+"}")) {
					dest = dest.replace("${"+property.toString()+"}",vars.get(property.toString()).toString());
				}
			}
			result.add(new String[]{source,dest});
		}
		return result;
	}

	/**
	 * @param vars
	 * @return
	 */
	public abstract List<String[]> getFileMappings(Map<String, Object> vars);

	/**
	 * 
	 * @param vars
	 * @return
	 */
	public FilterSetCollection getFilterSetCollection(Map vars) {
		return new FilterSetCollection(SeamFacetFilterSetFactory.createFiltersFilterSet(vars));
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
}
