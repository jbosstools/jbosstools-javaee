/*******************************************************************************
* Copyright (c) 2007-2008 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributor:
*     Red Hat, Inc. - initial API and implementation
******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.jsf.vpe.jsf.test.JsfTestPlugin;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Test class for JBIDE-3030
 * 
 * @author mareshkau
 * 
 */
public class JBIDE3030Test extends VpeTest {

	private static  Set<String> fileNames = new HashSet<String>();

	static {
		String jbide3030Path= JsfTestPlugin.getPluginResourcePath()
		+File.separator+"JBIDE-3030"+File.separator;//$NON-NLS-1$ 
		fileNames.add(jbide3030Path+"test.jsp"); //$NON-NLS-1$ 
		fileNames.add(jbide3030Path+"test.html");  //$NON-NLS-1$
		fileNames.add(jbide3030Path+"test.xhtml"); //$NON-NLS-1$
		fileNames.add(jbide3030Path+"jbide3385.jsp"); //$NON-NLS-1$
	}
	
	
	public JBIDE3030Test(String name) {
		super(name);
	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testJBIDE3030() throws Exception {
		
		setException(null);
		
		for (String fileName : fileNames) {
			
			File file = new File(fileName);
			
			assertTrue("File doesn't exists", file.isFile()); //$NON-NLS-1$
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
			
			IEditorPart editorPart = IDE.openEditorOnFileStore(page, fileStore );
			/**
			 * Test Case for https://jira.jboss.org/jira/browse/JBIDE-4786 
			 * and for https://jira.jboss.org/jira/browse/JBIDE-4786
			 * 
			 * @author mareshkau
			 * 
			 */		
			JSPMultiPageEditor jspMultiPageEditor = (JSPMultiPageEditor) editorPart;
			TestUtil.getVpeController(jspMultiPageEditor).visualRefresh();
			TestUtil.delay(5);
			TestUtil.waitForIdle();
			//end of lines for testing visual refresh on external files
			
			assertNotNull(editorPart);
			
			assertTrue("Editor Part should be instance of JSPMultipageEditor",  //$NON-NLS-1$
					editorPart instanceof JSPMultiPageEditor);
			
			page.closeAllEditors(false);
			
			if(getException()!=null) {
				
				throw new Exception(getException());
			}
		}
	}
}
