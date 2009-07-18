 /*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.project.facet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Alexey Kazakov
 * This class helps New Seam Wizard Page to create EJB, EAR and test projects for seam 2.* WAR project.
 */
public class Seam2ProjectCreator extends SeamProjectCreator {

	private static final String STRICT = "strict";
	private static final String MODULE_ORDER = "module-order";
	
	// test/*.jar are duplicated here since the filtering seem to be assymetric when matching 
	private static AntCopyUtils.FileSet JBOSS_TEST_LIB_FILESET = new AntCopyUtils.FileSet()
	    .include("testng\\.jar") //$NON-NLS-1$
		.include("test/hibernate-all\\.jar") //$NON-NLS-1$
		.include("hibernate-all\\.jar") //$NON-NLS-1$
		.include("test/jboss-embedded-all.jar") //$NON-NLS-1$
		.include("jboss-embedded-all.jar") //$NON-NLS-1$
		.include("test/jboss-embedded-api.jar") //$NON-NLS-1$
		.include("jboss-embedded-api.jar") //$NON-NLS-1$
		.include("test/jboss-deployers.jar") //$NON-NLS-1$		
		.include("jboss-deployers.jar") //$NON-NLS-1$
		.include("test/thirdparty-all\\.jar") //$NON-NLS-1$
		.include("thirdparty-all\\.jar") //$NON-NLS-1$
		.include("core.jar") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude(".*/\\.svn"); //$NON-NLS-1$

	/**
	 * @param model Seam facet data model
	 * @param seamWebProject Seam web project
	 */
	public Seam2ProjectCreator(IDataModel model, IProject seamWebProject) {
		super(model, seamWebProject);
		viewFilterSetCollection.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));
		droolsLibFolder = new File(seamHomePath, Seam2FacetInstallDelegate.DROOLS_LIB_SEAM_RELATED_PATH);
	}

	@Override
	protected void createTestProject() {
		File testProjectDir = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(), testProjectName); //$NON-NLS-1$
		testProjectDir.mkdir();

		IVirtualComponent component = ComponentCore.createComponent(seamWebProject);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

		File testLibDir = new File(testProjectDir,"lib"); //$NON-NLS-1$
		File embededEjbDir = new File(testProjectDir,"bootstrap"); //$NON-NLS-1$
		File testSrcDir = new File(testProjectDir,"test-src"); //$NON-NLS-1$
		String seamGenResFolder = seamRuntime.getResourceTemplatesDir();
		File dataSourceFile = new File(seamGenResFolder, "datasource-ds.xml");
		File seamPropertiesFile = new File(seamGenResFolder, "seam.properties");
		//File jbossBeansFile = new File(seamGenResFolder ,"META-INF/jboss-beans.xml"); //$NON-NLS-1$
		FilterSet filterSet = new FilterSet();
		filterSet.addFilter("projectName", seamWebProject.getName()); //$NON-NLS-1$
		filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(seamWebProject)); //$NON-NLS-1$
		filterSet.addFilter("webRootFolder",webRootVirtFolder.getUnderlyingFolder().getFullPath().removeFirstSegments(1).toString()); //$NON-NLS-1$

		FilterSet jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
		// TODO: why are these filters not shared!?
		filterSet.addConfiguredFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));

		AntCopyUtils.FileSet includeLibs = new AntCopyUtils.FileSet(getJBossTestLibFileset()).dir(new File(seamRuntime.getHomeDir(),"lib")); //$NON-NLS-1$
		AntCopyUtils.FileSet secondSetincludeLibs = new AntCopyUtils.FileSet(getJBossTestLibFileset()).dir(new File(seamRuntime.getHomeDir(),"lib/test")); //$NON-NLS-1$

		File[] firstlibs = includeLibs.getDir().listFiles(new AntCopyUtils.FileSetFileFilter(includeLibs));
		File[] secondLibs = secondSetincludeLibs.getDir().listFiles(new AntCopyUtils.FileSetFileFilter(secondSetincludeLibs));
		Set<String> allLibs = new HashSet<String>(); // HACK: needed to be unique because some jboss-*.jars are duplicated
		for(File f : firstlibs) {
			allLibs.add(f.getName());
		}
		for(File f : secondLibs) {
			allLibs.add(f.getName());
		}

		StringBuffer testLibraries = new StringBuffer();

		if (!SeamCorePlugin.getDefault().hasM2Facet(seamWebProject)) {
			for (String file : allLibs) {
				testLibraries.append("\t<classpathentry kind=\"lib\" path=\"lib/" + file + "\"/>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}			
		}

		StringBuffer requiredProjects = new StringBuffer();
		requiredProjects.append(
				"\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + seamWebProject.getName() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
		if(!SeamFacetAbstractInstallDelegate.isWarConfiguration(model)) {
			requiredProjects.append("\n\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + ejbProjectName + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
		} 
		filterSet.addFilter("testLibraries",testLibraries.toString()); //$NON-NLS-1$
		filterSet.addFilter("requiredProjects",requiredProjects.toString()); //$NON-NLS-1$
		File testTemplateDir = null;
		try {
			testTemplateDir = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "test-seam2"); //$NON-NLS-1$
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		AntCopyUtils.FileSet excludeCvsSvn 
				 = new AntCopyUtils.FileSet(SeamFacetAbstractInstallDelegate.CVS_SVN).dir(testTemplateDir);

		AntCopyUtils.copyFilesAndFolders(
				testTemplateDir,
				testProjectDir,
				new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
				new FilterSetCollection(filterSet), true);

		excludeCvsSvn.dir(new File(seamRuntime.getHomeDir(), "bootstrap")); //$NON-NLS-1$
		AntCopyUtils.copyFilesAndFolders(
				new File(seamRuntime.getHomeDir(), "bootstrap"), //$NON-NLS-1$
				embededEjbDir,
				new AntCopyUtils.FileSetFileFilter(excludeCvsSvn), new FilterSetCollection(), true);

//			AntCopyUtils.copyFileToFile(
//					persistenceFile,
//					new File(testProjectDir,"test-src/META-INF/persistence.xml"), //$NON-NLS-1$
//					new FilterSetCollection(filterSet), true);

		FilterSetCollection f = new FilterSetCollection();
		f.addFilterSet(filterSet);
		f.addFilterSet(jdbcFilterSet);

		AntCopyUtils.copyFileToFile(
				dataSourceFile,
				new File(testProjectDir, "test-src/META-INF/"+seamWebProject.getName() + "-test-ds.xml"), //$NON-NLS-1$
				f, true);

		AntCopyUtils.copyFileToFolder(
				seamPropertiesFile,
				testSrcDir, //$NON-NLS-1$
				new FilterSetCollection(filterSet), true);

		// Add "org.jboss.seam.core.init.debug=false" for Seam 2.1/2.2
		// to seam.properties file to avoid https://jira.jboss.org/jira/browse/JBIDE-3623
		if(getVersion() == SeamVersion.SEAM_2_1 || getVersion() == SeamVersion.SEAM_2_2) {
			Properties seamProperties = new Properties();
			File testSeamPropertiesFile = new File(testSrcDir, "seam.properties");
			FileInputStream inStream = null;
			FileOutputStream out = null;
			try {
				inStream = new FileInputStream(testSeamPropertiesFile);
				seamProperties.load(inStream);
				seamProperties.setProperty("org.jboss.seam.core.init.debug", "false"); //$NON-NLS-1$ //$NON-NLS-2$
				
				out = new FileOutputStream(testSeamPropertiesFile);
				seamProperties.store(out, "debug is explicitly disabled in test to avoid JBIDE-3623");
			} catch (FileNotFoundException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			} finally {
				if(inStream!=null) {
					try { inStream.close();	} catch (IOException e) { /**ignore*/ }					
					}									
				if(out!=null) {
					try { out.close();	} catch (IOException e) { /**ignore*/ }
				}
			}
		}

		if (!SeamCorePlugin.getDefault().hasM2Facet(seamWebProject)) {
			AntCopyUtils.copyFiles(new File(seamRuntime.getHomeDir(), "lib"), //$NON-NLS-1$
							testLibDir, new AntCopyUtils.FileSetFileFilter(includeLibs));

			// seam2 has a lib/test
			AntCopyUtils.copyFiles(new File(seamRuntime.getHomeDir(), "lib/test"), //$NON-NLS-1$
							testLibDir, new AntCopyUtils.FileSetFileFilter(includeLibs));
		}

		SeamFacetAbstractInstallDelegate.createComponentsProperties(testSrcDir, "", true); //$NON-NLS-1$
	}

	@Override
	protected void createEjbProject() {
		super.createEjbProject();
		// Copy security.drl to source folder
		AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "security.drl"), new File(ejbProjectFolder, "ejbModule/"), true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void configureJBossAppXml() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(earProjectName);
		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualFolder folder = component.getRootFolder();
		IFolder rootFolder = (IFolder) folder.getUnderlyingFolder();
		IResource jbossAppXml = rootFolder.findMember("META-INF/jboss-app.xml");
		if(jbossAppXml==null || !(jbossAppXml instanceof IFile) || !jbossAppXml.exists()) {
			return;
		}
		
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			return;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForEdit((IFile)jbossAppXml);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();
				Element root = document.getDocumentElement();
				if(root==null) {
					return;
				}
				NodeList children = root.getChildNodes();
				boolean strictAdded = false;
				Node firstChild = null;
				for(int i=0; i<children.getLength(); i++) {
					Node currentNode = children.item(i);
					if(Node.ELEMENT_NODE == currentNode.getNodeType() && firstChild == null) {
						firstChild = currentNode;
					}
					if(Node.ELEMENT_NODE == currentNode.getNodeType() && MODULE_ORDER.equals(currentNode.getNodeName())) {
						setValue(document,currentNode,STRICT);
						strictAdded = true;
					}
				}
				if (!strictAdded) {
					Element moduleOrder = document.createElement(MODULE_ORDER);
					setValue(document,moduleOrder,STRICT);
					if (firstChild != null) {
						root.insertBefore(moduleOrder, firstChild);
					} else {
						root.appendChild(moduleOrder);
					}
				}
				model.save();
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
		try {
			new FormatProcessorXML().formatFile((IFile) jbossAppXml);
		} catch (Exception ignore) {
		}
	}
	
	private void setValue(Document document, Node node, String value) {
		Text text = document.createTextNode(value);
		node.appendChild(text);
	}

	protected AntCopyUtils.FileSet getJBossTestLibFileset() {
		return JBOSS_TEST_LIB_FILESET;
	}

	protected AntCopyUtils.FileSet getJbossEarContent() {
		return Seam2FacetInstallDelegate.JBOSS_EAR_CONTENT;
	}
}