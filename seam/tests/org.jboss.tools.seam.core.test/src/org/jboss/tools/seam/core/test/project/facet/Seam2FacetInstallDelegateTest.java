/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.core.test.project.facet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.EventManager;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

public class Seam2FacetInstallDelegateTest extends AbstractSeamFacetTest {

	protected static final String SEAM_2_0_0 = "Seam 2.0.0";
	IFacetedProject warProject;
	IFacetedProject earProject;
	
	private IProjectFacet seam2Facet;
	private IProjectFacetVersion seam2FacetVersion;
	private boolean suspendAllValidation;
	private boolean suspendXJobs;
	
	public Seam2FacetInstallDelegateTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		suspendAllValidation = ValidationFramework.getDefault().isSuspended();
		ValidationFramework.getDefault().suspendAllValidation(true);
		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener( EventManager.getManager() );
// 		commented to run tests on wtp 3.0.4 build		
//		ws.removeResourceChangeListener( ValManager.getDefault() );
		//EventManager.getManager().shutdown(); 
		
		suspendXJobs = XJob.isSuspended();
		XJob.setSuspended(true);
		assertSeamHomeAvailable();
		
		seam2Facet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seam2FacetVersion = seam2Facet.getVersion("2.0");
		
		File folder = getSeamHomeFolder();
		
		SeamRuntimeManager.getInstance().addRuntime(SEAM_2_0_0, folder.getAbsolutePath(), SeamVersion.SEAM_2_0, true);
		SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_2_0_0);
		IProject war = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("warprj");
		warProject = (war!=null ? ProjectFacetsManager.create(war, false,
				null): createSeamWarProject("warprj"));
		IProject ear = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("earprj");
		earProject = (ear!=null? ProjectFacetsManager.create(ear, false,
				null):createSeamEarProject("earprj"));
		
		//warProject.getProject().getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ValidationFramework.getDefault().suspendAllValidation(suspendAllValidation);
		XJob.setSuspended(suspendXJobs);
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(EventManager.getManager(), 
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | 
				IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_CHANGE);
//		ws.addResourceChangeListener(ValOperationManager.getDefault(), 
//				IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_BUILD);

	}

	public static final String SEAM_201GA_HOME_PROPERY = "jbosstools.test.seam.2.0.1.GA.home";

	protected String getSystemPropertyName() {
		return SEAM_201GA_HOME_PROPERY;
	}
	
	@Override
	protected IDataModel createSeamDataModel(String deployType) {
		
		IDataModel dataModel = super.createSeamDataModel(deployType);
		dataModel.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_2_0_0);
		
		return dataModel;
	}
	
	
	public void testWarLibs() throws CoreException {
		
		Set<String> seamgenlibs = new HashSet<String>();

		
		seamgenlibs.add("antlr-runtime.jar");
		seamgenlibs.add("commons-beanutils.jar");
		seamgenlibs.add("commons-digester.jar");
		seamgenlibs.add("drools-compiler.jar");
		seamgenlibs.add("drools-core.jar");
		seamgenlibs.add("core.jar");
		seamgenlibs.add("jboss-el.jar");
		seamgenlibs.add("jboss-seam-debug.jar");
		seamgenlibs.add("jboss-seam-ioc.jar");
		seamgenlibs.add("jboss-seam.jar");
		seamgenlibs.add("jboss-seam-mail.jar");
		seamgenlibs.add("jboss-seam-pdf.jar");
		seamgenlibs.add("jboss-seam-remoting.jar");
		seamgenlibs.add("jboss-seam-ui.jar");
		seamgenlibs.add("jbpm-jpdl.jar");
		seamgenlibs.add("jsf-facelets.jar");
		seamgenlibs.add("mvel14.jar");
		seamgenlibs.add("richfaces-api.jar");
		seamgenlibs.add("richfaces-impl.jar");
		seamgenlibs.add("richfaces-ui.jar");

		final IContainer warLibs = (IContainer) warProject.getProject().findMember("WebContent/WEB-INF/lib").getAdapter(IContainer.class);
		assertOnlyContainsTheseFiles(seamgenlibs, warLibs);
	}

	public void testMvelWarJars() {
		final IContainer warLibs = (IContainer) warProject.getProject().findMember("WebContent/WEB-INF/lib").getAdapter(IContainer.class);
		try {
			for (IResource resource : warLibs.members()) {
					if(resource.getName().matches("mvel.*\\.jar") ) {
						return;
					}
			}
			fail("mvel*.jar weren't found in seam 2.0. WAR project");
		} catch (CoreException e) {
			fail("Error occured during search mvel libraries in lib folder");
		}
	
	}

	public void testEarLibs() throws CoreException {
		
		IProject war = earProject.getProject();
		
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earProject.getProject());
		
		IProject ear = seamProjectsSet.getEarProject();
		
		Set<String> onlyInWar = new HashSet<String>();
		Set<String> onlyInEar = new HashSet<String>();
		Set<String> onlyInEarSeam = new HashSet<String>();
		Set<String> onlyInEjbSrc = new HashSet<String>();		

		onlyInEarSeam.add("jboss-seam.jar");
		onlyInEarSeam.add("lib");
		onlyInEarSeam.add("META-INF");
		
		onlyInEar.add("commons-beanutils.jar");
		onlyInEar.add("antlr-runtime.jar");
		onlyInEar.add("drools-compiler.jar");
		onlyInEar.add("drools-core.jar");
		onlyInEar.add("jboss-el.jar");
		onlyInEar.add("mvel14.jar");
		onlyInEar.add("richfaces-api.jar");		
		onlyInEar.add("jbpm-jpdl.jar");
		
		onlyInWar.add("commons-digester.jar");
		onlyInWar.add("jboss-seam-debug.jar");
		onlyInWar.add("jboss-seam-ioc.jar");
		onlyInWar.add("jboss-seam-mail.jar");
		onlyInWar.add("jboss-seam-pdf.jar");
		onlyInWar.add("jboss-seam-remoting.jar");
		onlyInWar.add("jboss-seam-ui.jar");
		onlyInWar.add("jsf-facelets.jar");
		onlyInWar.add("richfaces-impl.jar");
		onlyInWar.add("richfaces-ui.jar");
		
		final IContainer earLibsSeam = (IContainer) ear.findMember(new Path("EarContent")).getAdapter(IContainer.class);
		
		assertOnlyContainsTheseFiles(onlyInEarSeam, earLibsSeam);
		
		final IContainer earLibs = (IContainer) ear.findMember(new Path("EarContent").append("lib")).getAdapter(IContainer.class);
		
		assertOnlyContainsTheseFiles(onlyInEar, earLibs);
		
		final IContainer earMeta = (IContainer) ear.findMember("EarContent/META-INF").getAdapter(IContainer.class);
		
		Set onlyInEarMeta = new HashSet();
		
		onlyInEarMeta.add("jboss-app.xml");
		onlyInEarMeta.add("application.xml");
		
		assertOnlyContainsTheseFiles(onlyInEarMeta, earMeta);
		
		assertOnlyContainsTheseFiles(onlyInWar, (IContainer)war.findMember("WebContent/WEB-INF/lib").getAdapter(IContainer.class));

		IProject ejb = seamProjectsSet.getEjbProject();

		onlyInEjbSrc.add("security.drl");
		onlyInEjbSrc.add("seam.properties");
		onlyInEjbSrc.add("import.sql");
		onlyInEjbSrc.add("components.properties");
		onlyInEjbSrc.add("META-INF");				// JBIDE-2431: META-INF dir is always created by Seam 2.0 seamgen
		onlyInEjbSrc.add("org");					// JBIDE-2431: org dir is always created by Seam 2.0 seamgen
		
		assertOnlyContainsTheseFiles(onlyInEjbSrc, (IContainer)ejb.findMember("ejbModule").getAdapter(IContainer.class));		
	}

	public void testMvelEarJars() {
		IProject war = earProject.getProject();
		
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earProject.getProject());
		
		IProject ear = seamProjectsSet.getEarProject();
		final IContainer earLibs = (IContainer) ear.findMember(new Path("EarContent").append("lib")).getAdapter(IContainer.class);
			try {
			for (IResource resource : earLibs.members()) {
					if(resource.getName().matches("mvel.*\\.jar") ) {
						return;
					}
			}
			fail("mvel*.jar weren't found in seam 2.0. EAR project");
		} catch (CoreException e) {
			fail("Error occured during search mvel libraries in lib folder");
		}
	}

	/**
	 * Fails if set of fileNames is not found in dir or some other filename is found in dir.
	 * @param fileNames set of strings
	 * @param dir directory to scan
	 * @throws CoreException 
	 */
	protected void assertOnlyContainsTheseFiles(Set<String> fileNames,
			final IResource dir) throws CoreException {
		
		final Set<String> foundFiles = new HashSet<String>();
		dir.accept(new IResourceProxyVisitor() {
		    
			public boolean visit(IResourceProxy proxy) throws CoreException {
				if(dir.getName().equals(proxy.getName())) return true;
				foundFiles.add(proxy.getName());				
				return false;
			}
		
		}, IResource.DEPTH_ZERO);
		
		if(!foundFiles.containsAll(fileNames)) {
			fileNames.removeAll(foundFiles);
			fail("Did not find " + fileNames + " in " + dir);
		}
		
		foundFiles.removeAll(fileNames);
		
		assertTrue("Found additional files (" + foundFiles + " in " + dir + " at " + dir.getLocation(), foundFiles.isEmpty());		
	}

	public void testBootstrapDirPresent() throws CoreException, IOException {
		
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());
		
		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());
		
		assertNotNull(testProject.findMember("bootstrap"));
		//assertNotNull(testProject.findMember("bootstrap/data")); 
		
		assertNull("embedded-ejb should not be installed for seam2", testProject.findMember("embedded-ejb"));
		
	}

	public void testTestLibs() throws CoreException, IOException {
		
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());
		
		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());
		
		Set<String> libs = new HashSet<String>();
		libs.add("testng.jar");
		libs.add("hibernate-all.jar");
//		libs.add("jboss-deployers.jar");  // JBIDE-2431: There is no such jar created by Seam 2.0 seamgen
		libs.add("jboss-embedded-all.jar");
		libs.add("thirdparty-all.jar");
		libs.add("jboss-embedded-api.jar");
		libs.add("core.jar");		
		
		assertOnlyContainsTheseFiles(libs, testProject.findMember("lib"));
		
		// JBIDE-2431: The following block is commented because it duplicates the call to assertOnlyContainsTheseFiles()
/*
		assertNotNull(testProject.findMember("lib/testng.jar"));
		assertNotNull(testProject.findMember("lib/hibernate-all.jar"));
//		assertNotNull(testProject.findMember("lib/jboss-deployers.jar")); // JBIDE-2431: There is no such jar created by Seam 2.0 seamgen
		assertNotNull(testProject.findMember("lib/jboss-embedded-all.jar"));
		assertNotNull(testProject.findMember("lib/thirdparty-all.jar"));
		assertNotNull(testProject.findMember("lib/core.jar")); 				// JBIDE-2431: lib/core.jar file is always created by Seam 2.0 seamgen
*/
	}
	
	public void testSeamProperties() {
	SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());
		
		IProject warProject = warPs.getWarProject();
		assertTrue(warProject.exists());
		
		assertNotNull(warProject.findMember("src/main/seam.properties"));
		IResource findMember = warProject.findMember("src/hot/seam.properties");
		assertNotNull(findMember);
		assertTrue(findMember instanceof IFile);
		
	}
	
	public void testJbpmPresent() throws CoreException, IOException {
		
		SeamProjectsSet earPs = new SeamProjectsSet(earProject.getProject());
		
		IProject project = earPs.getEarProject();
		assertTrue(project.exists());
		
		assertNotNull(project.findMember("EarContent/lib/jbpm-jpdl.jar"));		
		
	}

	public void testDroolsPresent() throws CoreException, IOException {
		
		
		IProject earRoot = earProject.getProject();
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earRoot);
		
		// JBIDE-2431: security.drl is always created in <EJBProject>/ejbModule directory by Seam 2.0 seamgen
		IProject ejbProject = seamProjectsSet.getEjbProject();
		assertNotNull(ejbProject.findMember("ejbModule/security.drl"));		
		
	}
	
	public void testCreateEar() throws CoreException, IOException {
		
	}
		
	@Override
	protected IProjectFacetVersion getSeamFacetVersion() {
		return seam2FacetVersion;
	}
}
