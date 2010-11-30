/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.test;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * Junit test for JBIDE-4373
 * 
 * @author mareshkau
 *
 */
public class OpenOnForDecorateTest extends VpeTest {

	public OpenOnForDecorateTest(String name) {
		super(name);
	}

	/**
	 * Test openOn mechanism for VpeDefineContainerTemplate 
	 * in Seam's s:decorate template (SeamDecorateTemplate). 
	 * 
	 * @throws CoreException
	 * @throws IOException 
	 */
	public void testOpenOnForSDecorate() throws CoreException, IOException {
		VpeController vpeController = openInVpe(SeamAllTests.IMPORT_PROJECT_NAME, "JBIDE/4373/s-decorate.xhtml"); //$NON-NLS-1$
		int position = TestUtil.getLinePositionOffcet(vpeController.getSourceEditor().getTextViewer(), 11, 33);
		Node sourceNode = SelectionUtil.getNodeBySourcePosition(vpeController.getSourceEditor(), position);
		nsIDOMNode domNode = vpeController.getDomMapping().getNearVisualNode(sourceNode);
		vpeController.getSourceBuilder().openOn(domNode);
		IEditorPart  activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals("/templates/insert.xhtml file should be opened","insert.xhtml", activeEditor.getEditorInput().getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
