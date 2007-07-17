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
package org.jboss.tools.seam.internal.core.project.facet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamFacetInstallDelegete extends Object implements IDelegate {

	public static String PROFILE = "dev-war";
	
	public static FileSet TOMCAT_WAR_LIB_FILESET = new FileSet()
		.include("activation\\.jar")
		.include("ajax4jsf*.\\.jar")
		.include("commons-beanutils.*\\.jar")
		.include("commons-codec.*\\.jar")
		.include("commons-collections.*\\.jar")
		.include("commons-digester.*\\.jar")
		.include("commons-el.*\\.jar")
		.include("commons-lang.*\\.jar")
		.include("hibernate-all\\.jar")
		.include("itext.*\\.jar")
		.include("jboss-aop-jdk50\\.jar")
		.include("jboss-cache-jdk50\\.jar")
		.include("jboss-ejb3-all\\.jar")
		.include("jboss-seam-debug\\.jar")
		.include("jboss-seam-ui\\.jar")
		.include("jboss-seam\\.jar")
		.include("jcaptcha-all.*\\.jar")
		.include("jgroups\\.jar")
		.include("jsf-facelets\\.jar")
		.include("jstl.*\\.jar")
		.include("mail-ra\\.jar")
		.include("mail\\.jar")
		.include("mc-conf\\.jar")
		.include("myfaces-api.*\\.jar")
		.include("myfaces-impl.*\\.jar")
		.include("oscache.*\\.jar")
		.include("portlet-api-lib\\.jar")
		.include("richfaces.*\\.jar")
		.include("spring\\.jar")
		.include("thirdparty-all\\.jar");
	
	public static FileSet JBOSS_WAR_LIB_FILESET = new FileSet() 
		.include("ajax4jsf.*\\.jar")
		.include("antlr.*\\.jar")
		.include("antlr.*\\.jar")
		.include("commons-beanutils.*\\.jar")
		.include("commons-collections.*\\.jar")
		.include("commons-digester.*\\.jar")
		.include("commons-jci-core.*\\.jar")
		.include("commons-jci-janino.*\\.jar")
		.include("drools-compiler.*\\.jar")
		.include("drools-core.*\\.jar")
		.include("janino.*\\.jar")
		.include("jboss-seam-debug\\.jar")
		.include("jboss-seam-ioc\\.jar")
		.include("jboss-seam-mail\\.jar")
		.include("jboss-seam-pdf\\.jar")
		.include("jboss-seam-remoting\\.jar")
		.include("jboss-seam-ui\\.jar")
		.include("jboss-seam\\.jar")
		.include("jbpm.*\\.jar")
		.include("jsf-facelets\\.jar")
		.include("oscache.*\\.jar")
		.include("stringtemplate.*\\.jar")
		.include("testng-.*\\.jar");
	
	public static FileSet EAR_LIB_FILESET  = new FileSet()
		.include("jboss-aop-jdk50\\.jar")
		.include("jboss-cache-jdk50\\.jar")
		.include("jboss-seam\\.jar")
		.include("jgroups\\.jar");

	public static FileSet VIEW_FILESET = new FileSet()
		.include("home\\.xhtml")
		.include("error\\.xhtml")
		.include("login\\.xhtml")
		.include("login\\.page.xml")
		.include("index\\.html")
		.include("layout")
		.include("layout/.*")
		.include("stylesheet/.*")
		.include("img/.*")
		.include("img")
		.exclude(".*/.*\\.ftl");
	
	public static FileSet JBOOS_WAR_WEBINF_SET = new FileSet()
		.include("WEB-INF/web\\.xml")
		.include("WEB-INF/pages\\.xml")
		.include("WEB-INF/jboss-web\\.xml")
		.include("WEB-INF/faces-config\\.xml")
		.include("WEB-INF/componets\\.xml");
	
	public static FileSet JBOOS_WAR_WEB_INF_CLASSES_SET = new FileSet()
		.include("import\\.sql")
		.include("security\\.drl")
		.include("seam\\.properties")
		.include("messages_en\\.properties");
	
	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib";
	
	public static String SEAM_LIB_RELATED_PATH = "lib";
	
	public static String WEB_LIBRARIES_RELATED_PATH = "WEB-INF/lib";
	
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel)config;

		// get WebContents folder path from DWP model 
		WebArtifactEdit edit = WebArtifactEdit.getWebArtifactEditForRead(project);
		IVirtualComponent com = ComponentCore.createComponent(project);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		IVirtualFolder srcRootFolder = com.getRootFolder().getFolder(new Path("/WEB-INF/classes"));
		IContainer folder = webRootFolder.getUnderlyingFolder();
		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());
		
		File webContentFolder = folder.getLocation().toFile();
		File webInfFolder = new File(webContentFolder,"WEB-INF");
		File webInfClasses = new File(webInfFolder,"classes");
		File webInfClassesMetaInf = new File(webInfClasses, "META-INF");
		webInfClassesMetaInf.mkdirs();
		File webLibFolder = new File(webContentFolder,WEB_LIBRARIES_RELATED_PATH);
		File srcFolder = srcRootFolder.getUnderlyingFolder().getLocation().toFile();
		File srcWebInf = new File(srcFolder, "META-INF");
		String seamHomePath = model.getProperty(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString();
		
		File seamHomeFolder = new File(seamHomePath);
		File seamLibFolder = new File(seamHomePath,SEAM_LIB_RELATED_PATH);
		File seamGenResFolder = new File(seamHomePath,"seam-gen/resources");
		File droolsLibFolder = new File(seamHomePath,DROOLS_LIB_SEAM_RELATED_PATH);
		File securityDrools = new File(seamGenResFolder,"security.drl");
		File seamGenHomeFolder = new File(seamHomePath,"seam-gen");
		File seamGenViewSource = new File(seamGenHomeFolder,"view");
		File dataSourceDsFile = new File(seamGenResFolder, "datasource-ds.xml");

		File jdbcDriverFile = new File(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH).toString());
		File hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch");
		File hibernateConsolePropsFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.properties");
		File hibernateConsolePref = new File(seamGenHomeFolder, "hibernatetools/.settings/org.hibernate.eclipse.console.prefs");
		File persistenceFile = new File(seamGenResFolder,"META-INF/persistence-" + PROFILE + ".xml");
		
		FilterSet jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
		FilterSet projectFilterSet =  SeamFacetFilterSetFactory.createProjectFilterSet(model);
		FilterSet filtersFilterSet =  SeamFacetFilterSetFactory.createFiltersFilterSet(model);
		
		// ****************************************************************
		// Copy view folder from seam-gen installation to WebContent folder
		// ****************************************************************
		
		FileSet viewFileSet = new FileSet(VIEW_FILESET).dir(seamGenViewSource);
		FilterSetCollection viewFilterSetCollection = new FilterSetCollection();
		viewFilterSetCollection.addFilterSet(jdbcFilterSet);
		viewFilterSetCollection.addFilterSet(projectFilterSet);
		AntCopyUtils.copyFilesAndFolders(
				seamGenViewSource, 
				webContentFolder, 
				new FileSetFileFilter(viewFileSet), 
				viewFilterSetCollection, 
				true);
		
		// *******************************************************************
		// Copy manifest and configuration resources the same way as view
		// *******************************************************************
		
		FileSet webInfSet = new FileSet(JBOOS_WAR_WEBINF_SET).dir(seamGenResFolder);
		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder,webContentFolder,new FileSetFileFilter(webInfSet), viewFilterSetCollection, true);
		
		FileSet webInfClassesSet = new FileSet(JBOOS_WAR_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder,srcFolder,new FileSetFileFilter(webInfClassesSet), viewFilterSetCollection, true);

		AntCopyUtils.copyFileToFile(
				dataSourceDsFile, 
				new File(srcFolder,project.getName()+"-ds.xml"), 
				viewFilterSetCollection, true);
		
		AntCopyUtils.copyFileToFile(
				hibernateConsoleLaunchFile, 
				new File(project.getLocation().toFile(),project.getName()+".launch"), 
				new FilterSetCollection(projectFilterSet), true);
		

		AntCopyUtils.copyFileToFile(
				persistenceFile, 
				new File(srcWebInf,"persistence.xml"), 
				new FilterSetCollection(projectFilterSet), true);		
		
		FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(jdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(projectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));
		
		AntCopyUtils.copyFileToFolder(
				hibernateConsolePropsFile, 
				project.getLocation().toFile(),
				hibernateDialectFilterSet, true);
		
		// add copy for /hibernatetools/seam-gen.reveng.xml
		
		
		AntCopyUtils.copyFileToFolder(
				hibernateConsolePref,
				new File(project.getLocation().toFile(),".settings"),
				new FilterSetCollection(projectFilterSet), true);
		
		// ********************************************************************************************
		// Copy libraries libraries (seam jars, seam dependencies jars, drols jars, jdbc jar)
		// ********************************************************************************************
		copyFiles(seamHomeFolder,webLibFolder,new FileSetFileFilter(new FileSet(JBOSS_WAR_LIB_FILESET).dir(seamHomeFolder)));
		copyFiles(seamLibFolder,webLibFolder,new FileSetFileFilter(new FileSet(JBOSS_WAR_LIB_FILESET).dir(seamLibFolder)));
		copyFiles(droolsLibFolder,webLibFolder,new FileSetFileFilter(new FileSet(JBOSS_WAR_LIB_FILESET).dir(droolsLibFolder)));
		copyFiles(seamHomeFolder,webLibFolder,new FileSetFileFilter(new FileSet(JBOSS_WAR_LIB_FILESET).dir(seamHomeFolder)));
		copyFiles(seamLibFolder,webLibFolder,new FileSetFileFilter(new FileSet(JBOSS_WAR_LIB_FILESET).dir(seamLibFolder)));		

		// ********************************************************************************************
		// Copy JDBC driver if there is any
		// ********************************************************************************************
		if(jdbcDriverFile.exists())
			AntCopyUtils.copyFileToFolder(jdbcDriverFile, webLibFolder, true);
		
		// Copy sources to src
		AntCopyUtils.copyFileToFile(
				new File(seamGenHomeFolder,"src/Authenticator.java"),
				new File(project.getLocation().toFile(),"src/" + model.getProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString().replace('.', '/')+"/"+"Authenticator.java"),
				new FilterSetCollection(filtersFilterSet), true);
		
		// TODO may be generate RHDS studio feature to show it on projects view

		// ********************************************************************************************
		// Handle WAR/EAR configurations
		// ********************************************************************************************
		if(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS).equals("war")) {
			AntCopyUtils.copyFileToFile(
					new File(seamGenResFolder,"WEB-INF/components-war.xml"),
					new File(webInfFolder,"components.xml"),
					new FilterSetCollection(projectFilterSet), true);

			// ********************************************************************************************
			// TODO replace with appropriate one to handle Dynamic Web Project Structure
			// ********************************************************************************************
			AntCopyUtils.copyFileToFile(
					new File(seamHomeFolder,"seam-gen/build-scripts/build-war.xml"),
					new File(project.getLocation().toFile(),"build.xml"),
					new FilterSetCollection(projectFilterSet), true);

			// ********************************************************************************************
			// Copy seam project indicator
			// ********************************************************************************************
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder,"seam.properties"), srcFolder, true);
			
		} else {
			// copy ear files
			AntCopyUtils.copyFileToFile(
					new File(seamGenResFolder,"WEB-INF/components.xml"),
					new File(webInfFolder,"components.xml"),
					new FilterSetCollection(projectFilterSet), true);
			AntCopyUtils.copyFileToFile(
					new File(seamHomeFolder,"seam-gen/build-scripts/build.xml"),
					new File(project.getLocation().toFile(),"build.xml"),
					new FilterSetCollection(projectFilterSet), true);	
		}
		
		writeXModel(project, model);
		
		
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		
		EclipseResourceUtil.addNatureToProject(project, "org.jboss.tools.jsf.jsfnature");
		EclipseResourceUtil.addNatureToProject(project, ISeamProject.NATURE_ID);

	}

	public static void copyFiles(File source, File dest, FileFilter filter) {
		dest.mkdir();
		for (File file:source.listFiles(filter)) {
			if(file.isDirectory())continue;
			try {
				FileUtils.getFileUtils().copyFile(file, new File(dest,file.getName()),new FilterSetCollection(),true);
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

	public static class FileSet {
		
		File dir = null;
		
		List<Pattern> include = new ArrayList<Pattern>();
		
		List<Pattern> exclude = new ArrayList<Pattern>();
		
		public FileSet(String dir) {
			this.dir = new File(dir);
		}
		
		public FileSet(File dir) {
			this.dir = dir;
		}
		
		public FileSet(FileSet template) {
			addTemplate(template);
		}
		
		public void addTemplate(FileSet template){
			include.addAll(template.getIncluded());
			exclude.addAll(template.getExcluded());
		}
		
		public FileSet() {
			
		}
		
		public FileSet add(FileSet set) {
			addTemplate(set);
			return this;
		}
		
		public FileSet dir(String dir) {
			this.dir = new File(dir);
			return this;
		}
		
		public FileSet dir(File dir) {
			this.dir = dir;
			return this;
		}
		
		public FileSet include(String pattern) {
			include.add(Pattern.compile(pattern));
			return this;
			
		}
		
		public FileSet exclude(String pattern) {
			exclude.add(Pattern.compile(pattern));
			return this;
		}
		
		public boolean isIncluded(String file) { 
			int i = dir.getAbsolutePath().length()+1;
			String relatedPath = file.substring(i);
			if(new File(file).isDirectory())return true;
			for (Pattern pattern : include) {			
				if(pattern.matcher(relatedPath.replace('\\', '/')).matches() ) {
					return !isExcluded(relatedPath);
				}
			}
			return false;
		}
		
		public boolean isExcluded(String file){
			for (Pattern pattern : exclude) {
				if(pattern.matcher(file.replace('\\', '/')).matches()) return true;
			}
			return false;	
		}
		
		public List<Pattern> getExcluded() {
			return Collections.unmodifiableList(exclude);
		}
		
		public List<Pattern> getIncluded() {
			return Collections.unmodifiableList(include);
		}

		/**
		 * @return
		 */
		public File getDir() {
			return dir;
		}
	}
	
	public static class FileSetFileFilter implements FileFilter {
		
		FileSet set;
		public FileSetFileFilter(FileSet set) {
			this.set = set;
		}
		
		public boolean accept(File pathname){
			return set.isIncluded(pathname.getAbsolutePath());
		}
	}

	private void writeXModel(IProject project, IDataModel model) {
		String projectName = project.getName();
		String webContent = "WebContent";
		
		//TODO This returns null. Why? How else can we get WebContent folder name?
		webContent = (String)model.getProperty(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER);
		
		if(webContent == null) {
			webContent = "WebContent";
		}
		String src = "src";
		
		String[] srcs = EclipseResourceUtil.getJavaProjectSrcLocations(project);
		if (srcs.length > 0) {
			src = srcs[0].replace('\\','/').substring(srcs[0].lastIndexOf('/') + 1);
		}
		File location = new File(project.getLocation().toFile(),".settings/org.jboss.tools.jst.web.xml");
		
		Object[] arguments = {
			projectName,
			webContent,
			src
		};
		String body = MessageFormat.format(XMODEL, arguments);
		
		org.jboss.tools.common.util.FileUtil.writeFile(location, body);
	}
	
	/**
	 * {0} - project name
	 * {1} - WebContent folder name
	 * {2} - src folder name
	 */
	private static String XMODEL = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<FILESYSTEMS APPLICATION_NAME=\"{0}\" ENTITY=\"FileSystems\"" +
		" VERSION=\"2.0.0\" WORKSPACE_HOME=\"./{1}/WEB-INF\">" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\" LOCATION=\"%redhat.workspace%\" NAME=\"WEB-INF\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\" INFO=\"Content-Type=Web\"" +
		" LOCATION=\"%redhat.workspace%/..\" NAME=\"WEB-ROOT\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\"" +
		" LOCATION=\"%redhat.workspace%/../../{2}\" NAME=\"src\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\" LOCATION=\"%redhat.workspace%/lib\" NAME=\"lib\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\"" +
		" LOCATION=\"%redhat.workspace%/classes\" NAME=\"classes\"/>" +
		"<WEB ENTITY=\"JstWeb\" MODEL_PATH=\"/web.xml\" SERVLET_VERSION=\"2.4\">" + 
		"  <MODULE ENTITY=\"WebJSFModule\" MODEL_PATH=\"/faces-config.xml\"" +
		"   ROOT=\"WEB-ROOT\" SRC=\"src\" URI=\"/WEB-INF/faces-config.xml\"/>" +
		"</WEB>" +
		"</FILESYSTEMS>"
	;
}
