/******************************************************************************* 
* Copyright (c) 2007 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Red Hat, Inc. - initial API and implementation
******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.tld.ITaglibMapping;
import org.jboss.tools.jst.web.tld.TaglibMapping;
import org.jboss.tools.vpe.ui.test.ProjectsLoader;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author V.Kabanovich
 *
 */
public class JBIDE4213Test extends VpeTest {

	private static final String TEST_PAGE_NAME="JBIDE/1105/employee.xhtml"; //$NON-NLS-1$
	
	public JBIDE4213Test(String name) {
		super(name);
	}
	
	public void testJBIDE4213() throws Throwable {
	// wait
	TestUtil.waitForJobs();
	setException(null);

	IProject project = ProjectsLoader.getInstance()
			.getProject(JsfAllTests.IMPORT_CUSTOM_FACELETS_PROJECT);
	assertNotNull(project);

	IModelNature nature = EclipseResourceUtil.getModelNature(project);
	assertNotNull(nature);

	ITaglibMapping tm = WebProject.getInstance(nature.getModel()).getTaglibMapping();
	((TaglibMapping)tm).revalidate(WebAppHelper.getWebApp(nature.getModel()));
	XModelObject o = tm.getTaglibObject("http://mareshkau/tags");
	assertNotNull(o);

	if (getException() != null) {
	    throw getException();
	}
	}

}
