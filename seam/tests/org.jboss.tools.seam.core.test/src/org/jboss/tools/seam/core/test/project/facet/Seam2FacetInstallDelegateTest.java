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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.SeamProject;
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
	    
		seam2Facet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seam2FacetVersion = seam2Facet.getVersion("2.0");
		
		File folder = new File(System.getProperty("jbosstools.test.seam.2.0.0.home", "/home/max/work/products/jboss-seam-2.0.0.CR2"));
		
		SeamRuntimeManager.getInstance().addRuntime(SEAM_2_0_0, folder.getAbsolutePath(), SeamVersion.SEAM_2_0, true);
		SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_2_0_0);
	
		warProject = createSeamWarProject("warprj");
		earProject = createSeamEarProject("earprj");
		
		warProject.getProject().getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		
		
		
		super.setUp();
	}
	
	@Override
	protected IDataModel createSeamDataModel(String deployType) {
		
		IDataModel dataModel = super.createSeamDataModel(deployType);
		dataModel.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_2_0_0);
		
		return dataModel;
	}
	
	
	public void testWarLibs() {
		
		
		assertNotNull("could not find antlr-runtime.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/antlr-runtime.jar"));
			assertNotNull("could not find commons-beanutils.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/commons-beanutils.jar"));
			assertNotNull("could not find commons-digester.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/commons-digester.jar"));
			assertNotNull("could not find drools-compiler.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/drools-compiler.jar"));
			assertNotNull("could not find drools-core.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/drools-core.jar"));
			assertNotNull("could not find jboss-el.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-el.jar"));
			assertNotNull("could not find jboss-seam-debug.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam-debug.jar"));
			assertNotNull("could not find jboss-seam-ioc.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam-ioc.jar"));
			assertNotNull("could not find jboss-seam.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam.jar"));
			assertNotNull("could not find jboss-seam-mail.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam-mail.jar"));
			assertNotNull("could not find jboss-seam-pdf.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam-pdf.jar"));
			assertNotNull("could not find jboss-seam-remoting.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam-remoting.jar"));
			assertNotNull("could not find jboss-seam-ui.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jboss-seam-ui.jar"));
			assertNotNull("could not find jbpm-jpdl.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jbpm-jpdl.jar"));
			assertNotNull("could not find jsf-facelets.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/jsf-facelets.jar"));
			assertNotNull("could not find mvel14.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/mvel14.jar"));
			assertNotNull("could not find richfaces-api.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/richfaces-api.jar"));
			assertNotNull("could not find richfaces-impl.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/richfaces-impl.jar"));
			assertNotNull("could not find richfaces-ui.jar",warProject.getProject().findMember("WebContent/WEB-INF/lib/richfaces-ui.jar"));

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
		
		assertNotNull(testProject.findMember("lib/testng.jar"));
		assertNotNull(testProject.findMember("lib/hibernate-all.jar"));
		assertNotNull(testProject.findMember("lib/jboss-deployers.jar"));
		assertNotNull(testProject.findMember("lib/jboss-embedded-all.jar"));
		assertNotNull(testProject.findMember("lib/thirdparty-all.jar"));
		
		
		
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
