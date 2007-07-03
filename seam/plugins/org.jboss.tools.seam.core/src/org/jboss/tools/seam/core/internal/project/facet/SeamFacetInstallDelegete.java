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
package org.jboss.tools.seam.core.internal.project.facet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
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
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamFacetInstallDelegete extends Object implements IDelegate {

	public static String PROFILE = "dev-war";
	
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
	
	public static FileSet SEAM_JARS = new FileSet()
		.include("jboss-seam.*\\.jar")
		.exclude("jboss-seam-gen\\.jar");
	
	public static FileSet JAVA_LIBS = new FileSet()
		.include(".[^/]*\\.jar")
		.include(".[^/]*\\.zip");
	
	public static FileSet JBOOS_WAR_RESOURCE_SET1 = new FileSet()
		.include("META-INF/jboss-beans\\.xml")
		.include("WEB-INF/pages\\.xml")
		.include("WEB-INF/faces-config\\.xml")
		.include("WEB-INF/web\\.xml");
	
	public static FileSet JBOOS_JAR_RESOURCE_SET1 = new FileSet()
		.include("META-INF/ejb-jar\\.xml")
		.include("META-INF/persistence-" + PROFILE + "\\.xml" )
		.include("import-" + PROFILE + "\\.sql");

	
	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib";
	
	public static String SEAM_LIB_RELATED_PATH = "lib";
	
	public static String WEB_LIBRARIES_RELATED_PATH = "WEB-INF/lib";
	
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel)config;

		// get WebContents folder path from DWP model 
		WebArtifactEdit edit = 
			WebArtifactEdit.getWebArtifactEditForRead(project);
		IVirtualComponent com = ComponentCore.createComponent(project);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		IContainer folder = webRootFolder.getUnderlyingFolder();
		
		File webContentFolder = folder.getLocation().toFile();
		
		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());

		String seamHomePath = model.getProperty(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString();
		File seamHomeFolder = new File(seamHomePath);
		File seamLibFolder = new File(seamHomePath,SEAM_LIB_RELATED_PATH);
		File seamGenResFolder = new File(seamHomePath,"seam-gen/resources");
		File droolsLibFolder = new File(seamHomePath,DROOLS_LIB_SEAM_RELATED_PATH);
		File seamGenViewSource = new File(seamHomePath,"seam-gen/view");
		File jdbcDriverFile = new File(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH).toString());
		File hibernateConsoleLaunchFile = new File(seamHomeFolder, "seam-gen/hibernatetools/hibernate-console.launch");
		File hibernateConsolePropsFile = new File(seamHomeFolder, "seam-gen/hibernatetools/hibernate-console.properties");
		
		
		
		FilterSet jdbcFilterSet = FilterSetFactory.createJdbcFilterSet(model);
		FilterSet projectFilterSet =  FilterSetFactory.createProjectFilterSet(model);
		
		// ****************************************************************
		// Copy view folder from seam-gen installation to WebContent folder
		// ****************************************************************
		
		FileSet viewFileSet = new FileSet(VIEW_FILESET).dir(seamGenViewSource);
		FilterSetCollection viewFilterSetCollection = new FilterSetCollection();
		viewFilterSetCollection.addFilterSet(jdbcFilterSet);
		viewFilterSetCollection.addFilterSet(projectFilterSet);
		AntCopyUtils.copyFilesAndFolders(
				seamGenViewSource, webContentFolder, new FileSetFileFilter(viewFileSet), viewFilterSetCollection, true);
		
		// *******************************************************************
		// Copy manifest and configuration resources the same way as view
		// *******************************************************************
		
		FileSet res1FileSet = new FileSet(JBOOS_WAR_RESOURCE_SET1).dir(seamGenResFolder);
		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder,webContentFolder,new FileSetFileFilter(res1FileSet), viewFilterSetCollection, true);
		
		AntCopyUtils.copyFileToFile(
				hibernateConsoleLaunchFile, 
				new File(project.getLocation().toFile(),project.getName()+".launch"), 
				new FilterSetCollection(projectFilterSet), true);
		
		FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(jdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(projectFilterSet);
		hibernateDialectFilterSet.addFilterSet(FilterSetFactory.createHibernateDialectFilterSet(model));
		
		AntCopyUtils.copyFileToFolder(
				hibernateConsolePropsFile, 
				project.getLocation().toFile(),
				hibernateDialectFilterSet, true);
		
		// TODO add copy for /hibernatetools/seam-gen.reveng.xml
		
		// *************************************
		// TODO modify existing faces-config.xml
		// *************************************
		
		
		// ********************************************************************************************
		// TODO copy libraries/link libraries (seam jars, seam dependencies jars, drols jars, jdbc jar)
		// ********************************************************************************************
		
		File webLibFolder = new File(webContentFolder,WEB_LIBRARIES_RELATED_PATH);
		copyFiles(seamHomeFolder,webLibFolder,new FileSetFileFilter(new FileSet(SEAM_JARS).dir(seamHomeFolder)));
		copyFiles(seamLibFolder,webLibFolder,new FileSetFileFilter(new FileSet(SEAM_JARS).dir(seamLibFolder)));
		copyFiles(droolsLibFolder,webLibFolder,new FileSetFileFilter(new FileSet(SEAM_JARS).dir(droolsLibFolder)));
		
		if(jdbcDriverFile.exists())
			AntCopyUtils.copyFile(jdbcDriverFile, webLibFolder, true);
		
		// TODO generate db support as seam-gen does
		
		// TODO may be generate RHDS studio feature to show it on projects view
		
		// TODO say JBoss AS adapter what kind of deployment to use
		
		// TODO generate build.xml
		
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	public static void copyFiles(File source, File dest, FileFilter filter) {
		dest.mkdir();
		for (File file:source.listFiles(filter)) {
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
			include.addAll(template.getIncluded());
			exclude.addAll(template.getExcluded());
		}
		
		public FileSet() {
			
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
	
	
	public static void main(String[] args) {
		System.out.println(Pattern.matches(".[^\\\\]*\\.jar", "tt\\test.jar"));
//		Properties props = new Properties();
//		props.put("hibernate.connection.username", "rooy");
//		FilterSet jdbcFs = FilterSetFactory.createJdbcFilterSet(props);
//		System.out.println(jdbcFs);
		FileSet include = VIEW_FILESET;
//			.dir("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view")
//			.include("home\\.xhtml")
//			.include("error\\.xhtml")
//			.include("login\\.xhtml")
//			.include("login\\.page.xml")
//			.include("index\\.html")
//			.include("layout\\.*")
//			.include("stylesheet\\.*")
//			.include("img\\.*")
//			.exclude(".*\\\\.*\\.ftl");
//		

		File file1 = new File("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view");
		FileSetFileFilter fileSetFilter = new FileSetFileFilter(include.dir(file1));
//		File[] copy = file1.listFiles(fileSetFilter);
//		for (File file : copy) {
//			System.out.println(file.getAbsolutePath());
//		}
//		copyFiles(file1,new File("c:\\temp\\4"),fileSetFilter);
		AntCopyUtils.copyFilesAndFolders(file1, new File("c:\\temp\\15"),fileSetFilter, new FilterSetCollection(), true);
	}
	
	public static class FilterSetFactory {
		
		public static FilterSet JDBC_TEMPLATE;
		public static FilterSet PROJECT_TEMPLATE;
		public static FilterSet FILTERS_TEMPLATE;
		public static FilterSet HIBERNATE_DIALECT_TEMPLATE;
		
		static {
			JDBC_TEMPLATE = new FilterSet();
			JDBC_TEMPLATE.addFilter("jdbcUrl","${hibernate.connection.url}");
			JDBC_TEMPLATE.addFilter("driverClass","${hibernate.connection.driver_class}");
			JDBC_TEMPLATE.addFilter("username","${hibernate.connection.username}");
			JDBC_TEMPLATE.addFilter("password","${hibernate.connection.password}");
			JDBC_TEMPLATE.addFilter("catalogProperty","${catalog.property}");
			JDBC_TEMPLATE.addFilter("schemaProperty","${schema.property}");
			
			PROJECT_TEMPLATE = new FilterSet();
			PROJECT_TEMPLATE.addFilter("projectName","${project.name}");
			PROJECT_TEMPLATE.addFilter("jbossHome","${jboss.home}");
			PROJECT_TEMPLATE.addFilter("hbm2ddl","${hibernate.hbm2ddl.auto}");
			PROJECT_TEMPLATE.addFilter("driverJar","${driver.file}");
			
			FILTERS_TEMPLATE = new FilterSet();
			FILTERS_TEMPLATE.addFilter("interfaceName","${interface.name}");
			FILTERS_TEMPLATE.addFilter("beanName","${bean.name}");
			FILTERS_TEMPLATE.addFilter("entityName","${entity.name}");
			FILTERS_TEMPLATE.addFilter("methodName","${method.name}");
			FILTERS_TEMPLATE.addFilter("componentName","${component.name}");
			FILTERS_TEMPLATE.addFilter("pageName","${page.name}");
			FILTERS_TEMPLATE.addFilter("masterPageName","${masterPage.name}");
			FILTERS_TEMPLATE.addFilter("actionPackage","${action.package}");
			FILTERS_TEMPLATE.addFilter("modelPackage","${model.package}");
			FILTERS_TEMPLATE.addFilter("testPackage","${test.package}");
			FILTERS_TEMPLATE.addFilter("listName","${component.name}List");
			FILTERS_TEMPLATE.addFilter("homeName","${component.name}Home");
			FILTERS_TEMPLATE.addFilter("query","${query.text}");
			
			HIBERNATE_DIALECT_TEMPLATE = new FilterSet();
			HIBERNATE_DIALECT_TEMPLATE.addFilter("hibernate.dialect","$hibernate.dialect");
		}
		
		public static FilterSet createJdbcFilterSet(IDataModel values) {
			return aplayProperties(JDBC_TEMPLATE, values);
		}
		public static FilterSet createProjectFilterSet(IDataModel values){
			return aplayProperties(PROJECT_TEMPLATE, values);
		}
		
		public static FilterSet createFiltersFilterSet(IDataModel values) {
			return aplayProperties(FILTERS_TEMPLATE, values);
		}
		
		public static FilterSet createHibernateDialectFilterSet(IDataModel values) {
			return aplayProperties(HIBERNATE_DIALECT_TEMPLATE, values);
		}
		
		private static FilterSet aplayProperties(FilterSet template,IDataModel values) {
			FilterSet result = new FilterSet();
			for (Object filter : template.getFilterHash().keySet()) {
				System.out.println(filter + "=" +template.getFilterHash().get(filter));
				String value = template.getFilterHash().get(filter).toString();
				for (Object property : values.getAllProperties()) {
					if(value.contains("${"+property.toString()+"}")) {
						value = value.replace("${"+property.toString()+"}",values.getProperty(property.toString()).toString());
					}
				}
				result.addFilter(filter.toString(), value);
			}
			return result;
		}
	}
	
	
}
