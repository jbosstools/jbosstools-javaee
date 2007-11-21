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
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
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
	
	public Seam2FacetInstallDelegateTest(String name) {
		super(name);
	}

	
	@Override
	protected void setUp() throws Exception {
		assertSeamHomeAvailable();
		
		seam2Facet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seam2FacetVersion = seam2Facet.getVersion("2.0");
		
		
		File folder = getSeamHomeFolder();
		
		SeamRuntimeManager.getInstance().addRuntime(SEAM_2_0_0, folder.getAbsolutePath(), SeamVersion.SEAM_2_0, true);
		SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_2_0_0);
	
		warProject = createSeamWarProject("warprj");
		earProject = createSeamEarProject("earprj");
		
		warProject.getProject().getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		
		
		
		super.setUp();
	}
	
	@Override
	protected File getSeamHomeFolder() {
		return new File(System.getProperty("jbosstools.test.seam.2.0.0.home", "/home/max/work/products/jboss-seam-2.0.0.GA"));
	}
	
	@Override
	protected IDataModel createSeamDataModel(String deployType) {
		
		IDataModel dataModel = super.createSeamDataModel(deployType);
		dataModel.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_2_0_0);
		
		return dataModel;
	}
	
	
	public void testWarLibs() throws CoreException {
		
		Set<String> warlibs = new HashSet<String>();

		warlibs.add("core.jar");
		warlibs.add("antlr-runtime.jar");
		warlibs.add("commons-beanutils.jar");
		warlibs.add("commons-digester.jar");
		warlibs.add("drools-compiler.jar");
		warlibs.add("drools-core.jar");
		warlibs.add("core.jar");
		warlibs.add("jboss-el.jar");
		warlibs.add("jboss-seam-debug.jar");
		warlibs.add("jboss-seam-ioc.jar");
		warlibs.add("jboss-seam.jar");
		warlibs.add("jboss-seam-mail.jar");
		warlibs.add("jboss-seam-pdf.jar");
		warlibs.add("jboss-seam-remoting.jar");
		warlibs.add("jboss-seam-ui.jar");
		warlibs.add("jbpm-jpdl.jar");
		warlibs.add("jsf-facelets.jar");
		warlibs.add("mvel14.jar");
		warlibs.add("richfaces-api.jar");
		warlibs.add("richfaces-impl.jar");
		warlibs.add("richfaces-ui.jar");

		final IContainer warLibs = (IContainer) warProject.getProject().findMember("WebContent/WEB-INF/lib").getAdapter(IContainer.class);
		assertOnlyContainsTheseFiles(warlibs, warLibs);
	

	}

	public void testEarLibs() throws CoreException {
		
		IProject war = earProject.getProject();
		
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earProject.getProject());
		
		IProject ear = seamProjectsSet.getEarProject();
		
		Set<String> onlyInWar = new HashSet<String>();
		Set<String> onlyInEar = new HashSet<String>();
		
		onlyInEar.add("jboss-seam.jar");
		onlyInEar.add("antlr-runtime.jar");
		onlyInEar.add("drools-compiler.jar");
		onlyInEar.add("drools-core.jar");
		onlyInEar.add("jboss-el.jar");
		onlyInEar.add("mvel14.jar");
		onlyInEar.add("richfaces-api.jar");
		onlyInEar.add("richfaces-api.jar");
		onlyInEar.add("jbpm-jpdl.jar");
		onlyInEar.add("META-INF");
		onlyInEar.add("security.drl");
		
		onlyInWar.add("commons-beanutils.jar");
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
				
		final IContainer earLibs = (IContainer) ear.findMember("EarContent").getAdapter(IContainer.class);
		
		assertOnlyContainsTheseFiles(onlyInEar, earLibs);
		
		//earLibs.findMember(path)
		
		
		assertOnlyContainsTheseFiles(onlyInWar, (IContainer)war.findMember("WebContent/WEB-INF/lib").getAdapter(IContainer.class));
		

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
		
		assertTrue("Found additional files (" + foundFiles + " in " + dir, foundFiles.isEmpty());		
	}

	public void testBootstrapDirPresent() throws CoreException, IOException {
		
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());
		
		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());
		
		assertNotNull(testProject.findMember("bootstrap"));
		assertNotNull(testProject.findMember("bootstrap/data"));
		
		assertNull("embedded-ejb should not be installed for seam2", testProject.findMember("embedded-ejb"));
		
	}

	public void testTestLibs() throws CoreException, IOException {
		
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());
		
		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());
		
		Set<String> libs = new HashSet<String>();
		libs.add("testng.jar");
		libs.add("hibernate-all.jar");
		libs.add("jboss-deployers.jar");
		libs.add("jboss-embedded-all.jar");
		libs.add("thirdparty-all.jar");
		libs.add("jboss-embedded-api.jar");
		
		assertOnlyContainsTheseFiles(libs, testProject.findMember("lib"));
		assertNotNull(testProject.findMember("lib/testng.jar"));
		assertNotNull(testProject.findMember("lib/hibernate-all.jar"));
		assertNotNull(testProject.findMember("lib/jboss-deployers.jar"));
		assertNotNull(testProject.findMember("lib/jboss-embedded-all.jar"));
		assertNotNull(testProject.findMember("lib/thirdparty-all.jar"));
		
		
		
	}
	
	public void testSeamProperties() {
	SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());
		
		IProject warProject = warPs.getWarProject();
		assertTrue(warProject.exists());
		
		assertNotNull(warProject.findMember("src/model/seam.properties"));
		IResource findMember = warProject.findMember("src/action/seam.properties");
		assertNotNull(findMember);
		assertTrue(findMember instanceof IFile);
		
	}
	
	public void testJbpmPresent() throws CoreException, IOException {
		
		SeamProjectsSet earPs = new SeamProjectsSet(earProject.getProject());
		
		IProject project = earPs.getEarProject();
		assertTrue(project.exists());
		
		assertNotNull(project.findMember("EarContent/jbpm-jpdl.jar"));		
		
	}

	public void testDroolsPresent() throws CoreException, IOException {
		
		
		IProject earRoot = earProject.getProject();
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earRoot);
		
		
		
		IProject earProject = seamProjectsSet.getEarProject();
		assertNotNull(earProject.findMember("EarContent/security.drl"));		
		
	}
	
	public void testCreateEar() throws CoreException, IOException {
		
		
	}
		
	@Override
	protected IProjectFacetVersion getSeamFacetVersion() {
		return seam2FacetVersion;
	}
}
