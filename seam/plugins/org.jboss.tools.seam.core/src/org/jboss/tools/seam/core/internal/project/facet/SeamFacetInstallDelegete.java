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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
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

public class SeamFacetInstallDelegete extends Object implements IDelegate {

	public static FileSet VIEW_FILESET = new FileSet()
		.include("home\\.xhtml")
		.include("error\\.xhtml")
		.include("login\\.xhtml")
		.include("login\\.page.xml")
		.include("index\\.html")
		.include("layout\\.*")
		.include("stylesheet\\.*")
		.include("img\\.*")
		.exclude(".*/.*\\.ftl");
	
	public static FileSet SEAM_JARS = new FileSet()
		.include("jboss-seam.*\\.jar")
		.exclude("jboss-seam-gen\\.jar");
	
	public static FileSet JAVA_LIBS = new FileSet()
		.include(".[^/]*\\.jar")
		.include(".[^/]*\\.zip");
	
	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib";
	
	public static String SEAM_RELATED_LIB = "lib";
	
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel)config;

		Properties propertiew = new Properties();
		
		// get WebContents folder path from model 
		WebArtifactEdit edit = 
			WebArtifactEdit.getWebArtifactEditForRead(project);
		IVirtualComponent com = ComponentCore.createComponent(project);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		IContainer folder = webRootFolder.getUnderlyingFolder();
		File webContentFolder = folder.getLocation().toFile();
		

		String seamHomeFolder = model.getProperty(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString();
		
		File seamGenViewSource = new File(seamHomeFolder,"seam-gen/view");
		
		// Copy view folder from seam-gen installation to

		FileSet viewFileSet = new FileSet(VIEW_FILESET).dir(seamGenViewSource.getAbsolutePath());
		FilterSetCollection viewFilterSetCollection = new FilterSetCollection();
		viewFilterSetCollection.addFilterSet(FilterSetFactory.createJdbcFilterSet(model));
		viewFilterSetCollection.addFilterSet(FilterSetFactory.createProjectFilterSet(model));
		AntCopyUtils.copyFilesAndFolders(
				seamGenViewSource, webContentFolder, new FileSetFileFilter(viewFileSet), viewFilterSetCollection, true);
//		File file1 = new File("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view");
//		File[] copy = file1.listFiles(fileSetFilter);
//		copyFiles(file1,new File("c:\\temp\\1"),fileSetFilter);
		
		
		// project location with filled out FIlterSet
		
		// TODO copy manifest and configuration resources the same way as view
		// TODO modify existing faces-config.xml
		
		// TODO copy libraries/link libraries
		File seamHome = new File(seamHomeFolder);
		File webLibFolder = new File(webContentFolder,"WEB-INF/lib");
		copyFiles(seamHome,webLibFolder,seamLibs);
		copyFiles(new File(seamHomeFolder,"lib"),webLibFolder,javaLibs);
		copyFiles(new File(seamHomeFolder,"drools/lib"),webLibFolder,javaLibs);
		
		String jdbcDriverFileName = model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH).toString();
		File jdbcDriverFile = new File(jdbcDriverFileName);
		if(jdbcDriverFile.exists())
			AntCopyUtils.copyFile(jdbcDriverFile, webLibFolder, true);
		
		// TODO generate db support as seam-gen does
		
		// TODO may be generate RHDS studio feature to show it on projects view
		
		// TODO say JBoss AS adapter what kind of deployment to use
		
		// TODO generate build.xml
		
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	protected void copyViewFolder(File viewSource, File viewDestination,HashMap<String, String> properties) {
		
		FilterSet filterSet = new FilterSet();
		for (Object	propertyName : properties.keySet()) {
			filterSet.addFilter(propertyName.toString(), properties.get(propertyName));
		}
		
		FilterSetCollection filters = new FilterSetCollection();
		filters.addFilterSet(filterSet);
		AntCopyUtils.copyFilesAndFolders(viewSource, viewDestination, filters, true);
		
	}
	
	protected void copySeamLibraries(File source, File dest) {
	}

	public static void copyFiles(File source, File dest, FileFilter filter) {
		dest.mkdir();
		for (File file:source.listFiles(filter)) {
			try {
				FileUtils.getFileUtils().copyFile(file, new File(dest,file.getName()),new FilterSetCollection(),true);
			} catch (IOException e) {
				// TODO add logging
			}
		}
	}
	
	static private FileFilter seamLibs = new FileFilter() {

		Pattern includePattern = Pattern.compile("jboss-seam.*\\.jar");
		Pattern excludePattern = Pattern.compile("jboss-seam-gen\\.jar");
		
		public boolean accept(File pathname){
			return 
				!excludePattern.matcher(pathname.getName()).matches() 
					&&
				includePattern.matcher(pathname.getName()).matches();
		}
	};
	
	static private FileFilter javaLibs = new FileFilter() {

		Pattern libs = Pattern.compile(".*\\.jar");
		Pattern zips = Pattern.compile(".*\\.zip");
		
		public boolean accept(File pathname){
			return 
				libs.matcher(pathname.getName()).matches() 
					||
				zips.matcher(pathname.getName()).matches();
		}
	};
	
	public static class FileSet {
		
		File dir = null;
		
		List<Pattern> include = new ArrayList<Pattern>();
		
		List<Pattern> exclude = new ArrayList<Pattern>();
		
		public FileSet(String dir) {
			this.dir = new File(dir);
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
			System.out.println(relatedPath);
			for (Pattern pattern : include) {			
				if(pattern.matcher(relatedPath).matches() ) {
					return !isExcluded(relatedPath);
				}
			}
			return false;
		}
		
		public boolean isExcluded(String file){
			for (Pattern pattern : exclude) {
				if(pattern.matcher(file).matches()) return true;
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
//		FileSet include = new FileSet()
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
//		FileSetFileFilter fileSetFilter = new FileSetFileFilter(include);
//		File file1 = new File("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view");
//		File[] copy = file1.listFiles(fileSetFilter);
//		copyFiles(file1,new File("c:\\temp\\1"),fileSetFilter);
//		AntCopyUtils.copyFilesAndFolders(file1, new File("c:\\temp\\12"),fileSetFilter, new FilterSetCollection(), true);
	}
	
	public static class FilterSetFactory {
		
		public static FilterSet JDBC_TEMPLATE;
		public static FilterSet PROJECT_TEMPLATE;
		public static FilterSet FILTERS_TEMPLATE;
		
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
