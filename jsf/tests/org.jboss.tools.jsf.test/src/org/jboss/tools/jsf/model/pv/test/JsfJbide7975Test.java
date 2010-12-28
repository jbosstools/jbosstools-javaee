/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.jsf.model.pv.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFPromptingProvider;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.TestProjectProvider;

public class JsfJbide7975Test extends TestCase {
	public static final String TEST_PROJECT_NAME = "JsfJbide7975";
	public static final String TEST_PROJECT_PATH = "/projects/" + TEST_PROJECT_NAME;

	TestProjectProvider prjProvider = null;
	IProject project = null;
	IModelNature nature = null;
	XModel model = null;
	
	JSFPromptingProvider provider = null;
	
	@Override
	protected void setUp() throws IOException, CoreException, InvocationTargetException, InterruptedException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);		
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(TEST_PROJECT_NAME);
		if(project==null) {
			prjProvider = new TestProjectProvider("org.jboss.tools.jsf.test", TEST_PROJECT_PATH, TEST_PROJECT_NAME, true);
			project = prjProvider.getProject();
		}
		
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		nature = EclipseResourceUtil.getModelNature(project);
		assertNotNull(nature);
		model = nature.getModel();
		assertNotNull(model);
		provider = new JSFPromptingProvider();

	}

	@Override
	protected void tearDown() throws Exception {
		if(prjProvider!=null) {
			prjProvider.dispose();
		}
	}
	
	public static final String PAGE_FILENAME = "/WebContent/page.jsp";
	public static final String PAGE_MARKER_PATTERN = "\"{0}\" cannot be resolved";
	public static final String PAGE_BUNDLE_PROPERTY = "property_one";
	public static final int PAGE_LINE = 14;

	public void testErrorMarkerAbsence() {
		try {
			assertMarkerIsNotCreatedForLine(PAGE_FILENAME, PAGE_MARKER_PATTERN, new Object[] {PAGE_BUNDLE_PROPERTY}, PAGE_LINE, true);
		} catch (CoreException ce) {
			fail("Error while getting markers");
		}
	}
	
	public static final int GET_BUNDLE_EXPECTED_LIST_SIZE = 2;
	
	public void testGetBundles() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_BUNDLES, "", new Properties());
		assertEquals("Bundles proposal list has wrong size",GET_BUNDLE_EXPECTED_LIST_SIZE, list.size());
	}

	public static final int GET_BUNDLE_PROPERTIES_EXPECTED_LIST_SIZE = 2;

	public void testGetBundleProperties() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_BUNDLE_PROPERTIES, "texts.msg", new Properties());
		assertEquals("Bundles properties proposal list has wrong size",GET_BUNDLE_PROPERTIES_EXPECTED_LIST_SIZE, list.size());
	}
	
	private void assertMarkerIsNotCreatedForLine(String fileName, String template, Object[] parameters, int lineNumber, boolean validate) throws CoreException{
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);

		if(validate) {
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
		}

		IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
		for (int i = 0; i < markers.length; i++) {
			String message = markers[i].getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
			int line = markers[i].getAttribute(IMarker.LINE_NUMBER, -1); //$NON-NLS-1$
			if(message.equals(messagePattern) && line == lineNumber){
				fail("Marker "+messagePattern+" for line - "+lineNumber+" has been found");
			}
		}
	}

}
