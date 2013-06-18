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
package org.jboss.tools.jsf.ui.operation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.jst.jsf.core.internal.project.facet.IJSFFacetInstallDataModelProperties;
import org.eclipse.jst.jsf.core.internal.project.facet.JSFFacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFAutoLoad;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jst.web.WebModelPlugin;
import org.jboss.tools.jst.web.WebUtils;
import org.jboss.tools.jst.web.context.RegisterServerContext;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.project.helpers.IWebProjectTemplate;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectContext;
import org.jboss.tools.jst.web.ui.operation.WebProjectCreationOperation;

public class JSFProjectCreationOperation extends WebProjectCreationOperation {
	
	public JSFProjectCreationOperation(IProject project, IPath projectLocation, RegisterServerContext registry, Properties properties) {
		super(project, projectLocation, registry, properties);
	}

	public JSFProjectCreationOperation(NewWebProjectContext context) {
		super(context);
	}

	protected String getNatureID() {
		return JSFNature.NATURE_ID;
	}

	protected void configFacets(IDataModel dataModel, String projectLocation) {
		super.configFacets(dataModel, projectLocation);

		FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
		IDataModel configJSF = (IDataModel) map.get("jst.jsf"); //$NON-NLS-1$
		if(configJSF == null) {
			configJSF = DataModelFactory.createDataModel(new JSFFacetInstallDataModelProvider());
			map.add(configJSF);
		}
		configJSF = (IDataModel) map.get("jst.jsf"); //$NON-NLS-1$
		if(configJSF != null) {
			String template = getProperty(TEMPLATE_VERSION_ID);
			String version = template.indexOf("1.1") >= 0 ? "1.1"
					: template.indexOf("1.2") >= 0 ? "1.2"
					: template.indexOf("2.0") >= 0 ? "2.0"
					: template.indexOf("2.1") >= 0 ? "2.1"
					: template.indexOf("2.2") >= 0 ? "2.2"
					: null;
			if(version != null) {
				configJSF.setProperty(IFacetDataModelProperties.FACET_VERSION_STR, version);
			}
			IProjectFacet facet = ProjectFacetsManager.getProjectFacet("jst.jsf"); //$NON-NLS-1$
			try {
				IFacetedProject fproj = ProjectFacetsManager.create(getProject(), true, new NullProgressMonitor());
				LibraryInstallDelegate libraryDelegate = new LibraryInstallDelegate(fproj, facet.getDefaultVersion());
				ILibraryProvider provider = LibraryProviderFramework.getProvider("jsf-no-op-library-provider"); //$NON-NLS-1$
				libraryDelegate.setLibraryProvider(provider);
				configJSF.setProperty(IJSFFacetInstallDataModelProperties.LIBRARY_PROVIDER_DELEGATE, libraryDelegate);
			} catch (CoreException e) {
				JsfUiPlugin.getDefault().logError(e);
			}
		}

	}

	protected IWebProjectTemplate createTemplate() {
		return new JSFTemplate();
	}

	protected String getLibLocation() {
		FileSystemImpl fs = (FileSystemImpl)FileSystemsHelper.getFileSystem(templateModel, "lib"); //$NON-NLS-1$
		if(fs != null) {
			return fs.getAbsoluteLocation();
		}
		fs = (FileSystemImpl)FileSystemsHelper.getWebInf(templateModel);
		return fs.getAbsoluteLocation() + "/lib"; //$NON-NLS-1$
	}
	
	protected void copyTemplate() throws Exception {
		String location = getProject().getLocation().toString();
		String location2 = location;

		String templateLocation = getTemplateLocation();
		String version = getProperty(TEMPLATE_VERSION_ID);

		File templateFile = new File(templateLocation);
		templateLocation = templateFile.getCanonicalPath().replace('\\', '/');
		
		File targetDir = new File(location);
		File targetDir2 = new File(location2);

		FileUtil.copyDir(templateFile, targetDir2, true, true);
		preprocessTemplate(templateFile, targetDir2);
		
		adjustProjectFile(targetDir, targetDir2);

		String jars[] = template.getLibraries(version);
		String libDir = getLibLocation();
		libDir = location2 + libDir.substring(templateLocation.length());

		for (int i = 0; i < jars.length; i++) {
			File source = new File(jars[i]); 
			FileUtil.copyFile(source, new File(libDir, source.getName()), true);
		}

		String servletVersion = getProperty(SERVLET_VERSION_ID);
		String[] servletJars = WebUtils.getServletLibraries(template.getTemplatesBase(), servletVersion);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < servletJars.length; sb.append(servletJars[i++]).append(';'));
		Properties buildProperties = new Properties();
		buildProperties.setProperty("classpath.external", sb.toString()); //$NON-NLS-1$

		File antDir = new File(location2 + "/ant"); //$NON-NLS-1$
		if(!antDir.exists()) antDir.mkdirs();
		OutputStream propFile =	new BufferedOutputStream(new FileOutputStream(location2 + "/ant/build.properties")); //$NON-NLS-1$
		try {		
			buildProperties.store(propFile, ""); //$NON-NLS-1$
		} finally {			
			propFile.close();
		}

		getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}
	
	private void adjustProjectFile(File targetDir, File targetDir2) {
		File f2 = getProjectFile(targetDir2);
		if(f2 != null) f2.delete();
	}	
	private File getProjectFile(File targetDir2) {
		File f = new File(targetDir2, IModelNature.PROJECT_FILE);
		if(f.exists()) return f;
		return null;
	}

	protected void postCreateWebNature() {
		if(projectFile != null) {
			if(projectFile.isFile()) {
				IFile f = EclipseResourceUtil.getFile(projectFile.getAbsolutePath());
				if(f != null && f.exists()) {
					try {
						f.delete(true, new NullProgressMonitor());
					} catch (CoreException e) {
						JSFModelPlugin.getPluginLog().logError(e);
						projectFile.delete();
					}
				} else {
					projectFile.delete();
				}
			}
			projectFile = null;
		}
		model.getProperties().put(XModelConstants.AUTOLOAD, new JSFAutoLoad());
	
		try {
//			EclipseResourceUtil.addNatureToProject(getProject(), IKbProject.NATURE_ID);
			WebModelPlugin.addNatureToProjectWithValidationSupport(getProject(), KbBuilder.BUILDER_ID, IKbProject.NATURE_ID);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

}
