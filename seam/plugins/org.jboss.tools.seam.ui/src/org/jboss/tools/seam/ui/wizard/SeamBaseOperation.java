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

package org.jboss.tools.seam.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.jboss.tools.jst.web.WebUtils;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetFilterSetFactory;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
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
	}

	private void putResourceLocationProperty(Map<String, Object> vars, String parameterName, IResource resource) {
		if(resource!=null) {
			vars.put(parameterName, resource.getLocation().toFile().toString());
		} else {
			vars.put(parameterName, "");
		}
	}

	private void putResourceLocationProperty(Map<String, Object> vars, String parameterName, String resourcePath) {
		if(resourcePath!=null) {
			vars.put(parameterName, resourcePath);
		} else {
			vars.put(parameterName, "");
		}
	}

	private void putPackageLocationProperty(Map<String, Object> vars, String parameterName, String packageName) {
		if(packageName!=null) {
			vars.put(parameterName, packageName.replace('.','/'));
		} else {
			vars.put(parameterName, "");
		}
	}

	protected IAdaptable info;

	/**
	 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus result = Status.OK_STATUS;
		this.info = info;

		final SeamProjectsSet seamPrjSet = new SeamProjectsSet(getProject(info));

		try {
			Map<String, Object> vars = loadParameters(info,	seamPrjSet);

			List<FileMapping> fileMapping = getFileMappings(vars);	
			List<String[]> fileMappingCopy = applyVariables(fileMapping,vars);
			FilterSetCollection filters = getFilterSetCollection(vars);
			final File[] file = new File[fileMappingCopy.size()];
			int index=0;
			for (String[] mapping : fileMappingCopy) {
				file[index] = new File(mapping[1]);
				FileUtils.getFileUtils().copyFile(new File(mapping[0]), file[index],filters,false);
				index++;
			}
			if(shouldTouchServer(seamPrjSet)) {
				WebUtils.changeTimeStamp(seamPrjSet.getWarProject());
			}
		} catch (BackingStoreException e) {
			result =  new Status(IStatus.ERROR,SeamGuiPlugin.PLUGIN_ID,e.getMessage(),e);
		} catch (IOException e) {
			result =  new Status(IStatus.ERROR,SeamGuiPlugin.PLUGIN_ID,e.getMessage(),e);
		} catch (CoreException e) {
			result =  new Status(IStatus.ERROR,SeamGuiPlugin.PLUGIN_ID,e.getMessage(),e);
		}
		finally {
			try {
				// ComponentCore is used to handle case when user changes
				// default WebContent folder to another one in
				// Web Facet configuration page
				IContainer viewFolder = seamPrjSet.getViewsFolder();
				if(viewFolder!=null) {
					IProject prj = seamPrjSet.getWarProject();
					IVirtualComponent webComp = ComponentCore.createComponent(prj);
					if(webComp!=null) {
						IVirtualFile manifest = webComp.getRootFolder().getFile("/META-INF/MANIFEST.MF");
						if(manifest!=null) {
							manifest.getUnderlyingFile().getParent().touch(monitor);
							manifest.getUnderlyingFile().touch(monitor);
						}
					}
				}

				// to keep workspace in sync				
				seamPrjSet.refreshLocal(monitor);
			} catch (CoreException e) {
				result =  new Status(IStatus.ERROR,SeamGuiPlugin.PLUGIN_ID,e.getMessage(),e);
			}
		}
		if (result.getSeverity()==IStatus.ERROR) {
			SeamGuiPlugin.getDefault().getLog().log(result);
		}
		return result;
	}

	private Map<String, Object> loadParameters(IAdaptable info,	SeamProjectsSet seamPrjSet) throws BackingStoreException {
		Map<String, INamedElement> params = (Map)info.getAdapter(Map.class);
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(seamPrjSet.getWarProject());
		
		Map<String, Object> vars = new HashMap<String, Object>();

		for (String key : seamFacetPrefs.keys()) {
			vars.put(key, seamFacetPrefs.get(key, "")); //$NON-NLS-1$
		}

		for (Object valueHolder : params.values()) {
			INamedElement elem  = (INamedElement)valueHolder;
			vars.put(elem.getName(),elem.getValue().toString());
		}

		loadCustomVariables(vars);

		String actionFolder = getSessionBeanPackageName(seamFacetPrefs, params);
		String entityFolder = getEntityBeanPackageName(seamFacetPrefs, params);
		String testFolder = getTestCasesPackageName(seamFacetPrefs, params);

		vars.put(IParameter.SEAM_PROJECT_INSTANCE, seamPrjSet.getWarProject());
		vars.put(IParameter.JBOSS_SEAM_HOME, SeamRuntimeManager.getInstance().getRuntimeForProject(seamPrjSet.getWarProject()).getHomeDir());

		putResourceLocationProperty(vars, IParameter.SEAM_PROJECT_LOCATION_PATH, seamPrjSet.getWarProject());
		putResourceLocationProperty(vars, IParameter.SEAM_PROJECT_WEBCONTENT_PATH, seamPrjSet.getViewsFolder());
		putResourceLocationProperty(vars, IParameter.SEAM_PROJECT_SRC_ACTION, seamPrjSet.getActionFolder());
		putResourceLocationProperty(vars, IParameter.SEAM_PROJECT_SRC_MODEL, seamPrjSet.getModelFolder());
		putResourceLocationProperty(vars, IParameter.SEAM_EJB_PROJECT_LOCATION_PATH, seamPrjSet.getEjbProject());
		putResourceLocationProperty(vars, IParameter.SEAM_TEST_PROJECT_LOCATION_PATH, seamPrjSet.getTestProject());
		putResourceLocationProperty(vars, IParameter.TEST_SOURCE_FOLDER, seamPrjSet.getTestsFolder());
		putPackageLocationProperty(vars, IParameter.SESSION_BEAN_PACKAGE_PATH, actionFolder);
		putResourceLocationProperty(vars, IParameter.SESSION_BEAN_PACKAGE_NAME, actionFolder);
		putPackageLocationProperty(vars, IParameter.TEST_CASES_PACKAGE_PATH, testFolder);
		putResourceLocationProperty(vars, IParameter.TEST_CASES_PACKAGE_NAME, testFolder);
		putPackageLocationProperty(vars, IParameter.ENTITY_BEAN_PACKAGE_PATH, entityFolder);
		putResourceLocationProperty(vars, IParameter.ENTITY_BEAN_PACKAGE_NAME, entityFolder);
		return vars;
	}

	public void openResultInEditor(final IAdaptable info) {
		final IProject project = getProject(info);
		final SeamProjectsSet seamPrjSet = new SeamProjectsSet(project);
		Map<String, Object> parameters = null;
		try {
			parameters = loadParameters(info, seamPrjSet);
		} catch (BackingStoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
			return;
		}
		final Map<String, Object> vars = parameters;
		final List<FileMapping> fileMappings = getFileMappings(parameters);
		Display display = Display.getCurrent();
		if(display!=null) {
			display.asyncExec(new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					openResultInEditorInCurrentThread(info, project, seamPrjSet, vars, fileMappings);
				}
			});
		} else {
			// If we run this methods from test environment then don't use asyncExec.
			openResultInEditorInCurrentThread(info, project, seamPrjSet, parameters, fileMappings);
		}
	}

	private static void openResultInEditorInCurrentThread(IAdaptable info, IProject project, SeamProjectsSet seamPrjSet, Map<String, Object> parameters, List<FileMapping> fileMappings) {
		try {
			if(!fileMappings.isEmpty()) {
				List<String[]> fileMappingCopy = applyVariables(fileMappings, parameters);
				IFile iFile = seamPrjSet.getWarProject().getWorkspace().getRoot().getFileForLocation(new Path(fileMappingCopy.get(0)[1]));
				IDE.openEditor(SeamGuiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), iFile);
			}
		} catch (PartInitException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

	protected boolean shouldTouchServer(SeamProjectsSet seamPrjSet) {
		return !seamPrjSet.isWarConfiguration();
	}

	protected String getSessionBeanPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
		return seamFacetPrefs.get(IParameter.SESSION_BEAN_PACKAGE_NAME, "");
	}

	protected String getEntityBeanPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
		return seamFacetPrefs.get(IParameter.ENTITY_BEAN_PACKAGE_NAME, "");
	}

	protected String getTestCasesPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
		return seamFacetPrefs.get(IParameter.TEST_CASES_PACKAGE_NAME, "");
	}

	protected IProject getProject(IAdaptable info) {
		Map<String, INamedElement> params = (Map)info.getAdapter(Map.class);

		return ResourcesPlugin.getWorkspace().getRoot().getProject(
				params.get(IParameter.SEAM_PROJECT_NAME).getValueAsString());
	}

	/**
	 * @param fileMapping
	 * @param vars
	 * @return
	 */
	public static List<String[]> applyVariables(List<FileMapping> fileMapping, Map<String, Object> vars) {
		List<String[]> result = new ArrayList<String[]>();
		for (FileMapping filter : fileMapping) {
			if(filter.getDeployType().equalsString((String)vars.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS)) &&
					(!filter.isTest() || Boolean.parseBoolean(vars.get(ISeamFacetDataModelProperties.TEST_CREATING).toString()))) { //$NON-NLS-1$
				String source = filter.getSource();
				for (Object property : vars.keySet()){
					if(source.contains("${"+property.toString()+"}")) { //$NON-NLS-1$ //$NON-NLS-2$
						source = source.replace("${"+property.toString()+"}",vars.get(property.toString()).toString()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				String dest = filter.getDestination();
				for (Object property : vars.keySet()){
					if(dest.contains("${"+property.toString()+"}")) { //$NON-NLS-1$ //$NON-NLS-2$
						dest = dest.replace("${"+property.toString()+"}",vars.get(property.toString()).toString()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				result.add(new String[]{source, dest});
			}
		}
		return result;
	}

	/**
	 * @param vars
	 * @return
	 */
	public abstract List<FileMapping> getFileMappings(Map<String, Object> vars);

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
		return new File(vars.get(IParameter.JBOSS_SEAM_HOME).toString(),"seam-gen");		 //$NON-NLS-1$
	}

	protected void loadCustomVariables(Map<String, Object> vars) {
	}
}