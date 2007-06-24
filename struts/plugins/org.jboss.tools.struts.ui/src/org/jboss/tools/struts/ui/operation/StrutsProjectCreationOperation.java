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
package org.jboss.tools.struts.ui.operation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.struts.StrutsProjectUtil;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.WebUtils;
import org.jboss.tools.jst.web.context.RegisterTomcatContext;
import org.jboss.tools.jst.web.project.helpers.IWebProjectTemplate;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectContext;
import org.jboss.tools.jst.web.ui.operation.WebNatureOperation;
import org.jboss.tools.jst.web.ui.operation.WebProjectCreationOperation;
import org.jboss.tools.struts.webprj.model.helpers.context.NewProjectWizardContext;

public class StrutsProjectCreationOperation extends WebProjectCreationOperation {

	public StrutsProjectCreationOperation(IProject project, IPath projectLocation, RegisterTomcatContext registry, Properties properties) {
		super(project, projectLocation, registry, properties);
		setProperty(WebNatureOperation.TLDS_ID, properties.getProperty(NewProjectWizardContext.ATTR_TLDS));
	}

	public StrutsProjectCreationOperation(NewWebProjectContext context) {
		super(context);
		setProperty(WebNatureOperation.TLDS_ID, context.getActionProperties().getProperty(NewProjectWizardContext.ATTR_TLDS));
	}
	
	protected IWebProjectTemplate createTemplate() {
		return new StrutsUtils();
	}

	protected String getNatureID() {
		return StrutsProjectUtil.STRUTS_NATURE_ID;
	}

	protected String getLibLocation() {
		FileSystemImpl fs = (FileSystemImpl)templateModel.getByPath("FileSystems/lib");
		if(fs != null) {
			return fs.getAbsoluteLocation();
		}
		return getWebInfLocation() + "/lib";
	}
	protected String getWebInfLocation() {
		FileSystemImpl fs = (FileSystemImpl)templateModel.getByPath("FileSystems/WEB-INF");
		return fs.getAbsoluteLocation();
	}
	
	protected void copyTemplate() throws Exception {
		String location = getProject().getLocation().toString();
		String location2 = location;
		if(isMultipleModulesProject()) {
			location2 += "/" + getProject().getName();
		}
		String templateLocation = getTemplateLocation();
		String version = getProperty(WebNatureOperation.TEMPLATE_VERSION_ID);

		File templateFile = new File(templateLocation);
		templateLocation = templateFile.getCanonicalPath().replace('\\', '/');
		
		File targetDir = new File(location);
		File targetDir2 = new File(location2);

		FileUtil.copyDir(templateFile, targetDir2, true, true);
		preprocessTemplate(templateFile, targetDir2);

		adjustProjectFile(targetDir, targetDir2);

//			copying selected TLDs
		String tldStr = getProperty(WebNatureOperation.TLDS_ID);
		if (tldStr != null)	{
			String webinf = getWebInfLocation();
			String webInfDir = location2 + webinf.substring(templateLocation.length());
			String tldDir = new StrutsUtils().getStrutsSupportTemplatesLocation(version) + "/tld";
			StringTokenizer tokenizer = new StringTokenizer(tldStr, ";");
			while (tokenizer.hasMoreTokens()) {
				String fileName = tokenizer.nextToken();
				FileUtil.copyFile(new File(tldDir, fileName), new File(webInfDir, fileName), true);
			}
		}

//			copying Struts jars		
		String strutsJars[] = template.getLibraries(version);
		String libDir = getLibLocation();
		libDir = location2 + libDir.substring(templateLocation.length());
		for (int i = 0; i < strutsJars.length; i++) {
			File source = new File(strutsJars[i]); 
			FileUtil.copyFile(source, new File(libDir, source.getName()), true);
		}

//			creating build.properties		
		String servletVersion = getProperty(WebNatureOperation.SERVLET_VERSION_ID);
		String servletJars[] = WebUtils.getServletLibraries(template.getTemplatesBase(), servletVersion);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < servletJars.length; sb.append(servletJars[i++]).append(';'));
		Properties buildProperties = new Properties();
		buildProperties.setProperty("classpath.external", sb.toString());

		File antDir = new File(location2 + "/ant");
		if(!antDir.exists()) antDir.mkdirs();
		OutputStream propFile =	new BufferedOutputStream(new FileOutputStream(location2 + "/ant/build.properties"));
		try {		
			buildProperties.store(propFile, "");
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
}
