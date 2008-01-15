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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author Max Areshkau
 * 
 * junit for http://jira.jboss.com/jira/browse/JBIDE-1568 
 *
 */
public class JsfJbide1568Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME="jsfTest";
	public JsfJbide1568Test(String name) {
		super(name);
	}
	
	 public void testJBIDE1568Body() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("JBIDE/1568/JBIDE-1568-body.jsp",IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	 
	 public void testJBIDE1568RichFaces() throws Throwable {
		 performTestForVpeComponent((IFile) TestUtil.getComponentPath("JBIDE/1568/JBIDE-1568-richfaces.jsp",IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	 }
	 
	 public void testJBIDE1568Seam() throws Throwable {
		 performTestForVpeComponent((IFile) TestUtil.getComponentPath("JBIDE/1568/JBIDE-1568-seam.xhtml",IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	 }
}
