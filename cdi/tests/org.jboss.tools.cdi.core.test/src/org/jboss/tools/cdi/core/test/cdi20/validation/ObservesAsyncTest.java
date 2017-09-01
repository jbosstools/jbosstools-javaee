/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.cdi20.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;
import org.osgi.framework.Bundle;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public class ObservesAsyncTest extends TestCase{
	
	protected IProject project;
	protected ICDIProject cdiProject;
	protected final static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	
	@Override
	protected void setUp() throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("cdi20");
		if(!project.exists()) {
			IProject project = ResourcesUtils.importProject(b, "/projects/cdi20");
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		}
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		TestUtil._waitForValidation(project);
		cdiProject = CDICorePlugin.getCDIProject(project, false);
	}
	
	public void testObserverAsyncConstructor() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ASYNC[cdiProject.getVersion().getIndex()], 30, 31);
	}
	
	public void testObserverAsyncWithPriority() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ASYNC_PRIORITY[cdiProject.getVersion().getIndex()], 39, 39);
	}
	
	public void testMultipleObserverAsyncParams() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.MULTIPLE_OBSERVING_PARAMETERS_ASYNC[cdiProject.getVersion().getIndex()], 43, 43);
	}
	
	public void testObserverAsyncWithInject() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ANNOTATED_INJECT_ASYNC[cdiProject.getVersion().getIndex()], 47,48);
	}
	
	public void testObserverAsyncProduces() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ASYNC[cdiProject.getVersion().getIndex()], 52,53);
	}
	
	public void testObserverAsyncDisposes() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ASYNC_PARAMETER_ILLEGALLY_ANNOTATED[cdiProject.getVersion().getIndex()], 57, 57);
	}
	
	public void testObserverAsyncWithObservesParam() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_AND_OBSERVER_ASYNC_ERROR[cdiProject.getVersion().getIndex()], 61, 61);
	}
	
	public void testObserverAsyncWithObservesMethod() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_AND_OBSERVER_ASYNC_METHOD_ERROR[cdiProject.getVersion().getIndex()], 65, 65);
	}
	
	public void testObserverAsyncInDecorator() throws CoreException {
		IFile file = project.getFile("src/cdi20/SimpleInterfaceDecorator.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ASYNC_IN_DECORATOR[cdiProject.getVersion().getIndex()], 23);
	}
	
	public void testObserverAsyncInInterceptor() throws CoreException {
		IFile file = project.getFile("src/cdi20/LoggingInterceptor.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ASYNC_IN_INTERCEPTOR[cdiProject.getVersion().getIndex()], 23);
	}
	
	public void testObserverAsyncInEjb1() throws CoreException {
		IFile file = project.getFile("src/cdi20/SessionEJB.java");
		String error = NLS.bind(CDIValidationMessages.ILLEGAL_OBSERVER_ASYNC_IN_SESSION_BEAN[cdiProject.getVersion().getIndex()], new String[]{"nonBusinessMethod", "SessionEJB"});
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, error, 35);
	}
	
	public void testObserverAsyncInEjb2() throws CoreException {
		IFile file = project.getFile("src/cdi20/SessionEJB.java");
		String error = NLS.bind(CDIValidationMessages.ILLEGAL_OBSERVER_ASYNC_IN_SESSION_BEAN[cdiProject.getVersion().getIndex()], new String[]{"finalNonBusinessMethod", "SessionEJB"});
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, error, 39);
	}
	
	public void testObserverAsyncInDependentBean() throws CoreException {
		IFile file = project.getFile("src/cdi20/DependentBean.java");
		new AbstractResourceMarkerTest().assertAnnotationIsCreated(file, CDIValidationMessages.ILLEGAL_CONDITIONAL_OBSERVER_ASYNC[cdiProject.getVersion().getIndex()], 10);
	}
	
	public void testEJBMarkersSize() throws CoreException {
		IFile file = project.getFile("src/cdi20/SessionEJB.java");
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(file, "org.jboss.tools.cdi.core.cdiproblem", ".*", true);
		assertTrue(markers.length == 2);
	}
	
	public void testIntereptorMarkersSize() throws CoreException {
		IFile file = project.getFile("src/cdi20/LoggingInterceptor.java");
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(file, "org.jboss.tools.cdi.core.cdiproblem", ".*", true);
		assertTrue(markers.length == 1);
	}
	
	public void testDecoratorMarkersSize() throws CoreException {
		IFile file = project.getFile("src/cdi20/SimpleInterfaceDecorator.java");
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(file, "org.jboss.tools.cdi.core.cdiproblem", ".*", true);
		assertTrue(markers.length == 1);
	}
	
	public void testBeanMarkersSize() throws CoreException {
		IFile file = project.getFile("src/cdi20/PaymentHandler.java");
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(file, "org.jboss.tools.cdi.core.cdiproblem", ".*", true);
		assertTrue(markers.length == 20);
	}
	
	public void testDependantBeanMarkersSize() throws CoreException {
		IFile file = project.getFile("src/cdi20/DependentBean.java");
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(file, "org.jboss.tools.cdi.core.cdiproblem", ".*", true);
		assertTrue(markers.length == 1);
	}

}
