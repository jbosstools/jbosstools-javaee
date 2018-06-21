/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.jsf23.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;
import org.osgi.framework.Bundle;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public class JSF23InjectionTest extends TestCase{
	
	protected IProject project;
	protected ICDIProject cdiProject;
	protected final static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	
	@Override
	protected void setUp() throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("jsf23");
		if(!project.exists()) {
			IProject project = ResourcesUtils.importProject(b, "/projects/jsf23");
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		}
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		TestUtil._waitForValidation(project);
		cdiProject = CDICorePlugin.getCDIProject(project, false);
	}
	
	public void testResourceHandlerInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 19);
	}
	
	public void testExternalContextInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 22);
	}

	public void testFacesContextInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 25);
	}
	
	public void testFlashInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 28);
	}

	public void testApplicationMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 31);
	}

	public void testRequestCookieMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 35);
	}

	public void testFlowMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 39);
	}

	public void testHeaderMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 43);
	}
	
	public void testHeaderValuesMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 47);
	}
	
	public void testInitParameterMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 51);
	}
	
	public void testRequestParameterMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 55);
	}
	
	public void testRequestParameterValuesMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 59);
	}
	
	public void testRequestMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 63);
	}
	
	public void testSessionMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 67);
	}
	
	public void testViewMapInjection() throws CoreException {
		IFile file = project.getFile("src/jsf23/MyBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[cdiProject.getVersion().getIndex()], 71);
	}
}
