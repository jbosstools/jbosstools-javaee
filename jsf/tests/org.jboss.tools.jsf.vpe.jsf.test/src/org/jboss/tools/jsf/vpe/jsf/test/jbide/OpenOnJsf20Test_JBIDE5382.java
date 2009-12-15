/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.NodesManagingUtil;
import org.jboss.tools.vpe.ui.test.OpenOnUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests for the OpenOn actions in JSF 2.0 projects. 
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
public class OpenOnJsf20Test_JBIDE5382 extends VpeTest {
	private static final String OUTPUT_STYLESHEET_ELEMENT_ID
			= "outputStylesheet1"; //$NON-NLS-1$
	private static final String OUTPUT_SCRIPT_ELEMENT_ID
			= "outputScript1"; //$NON-NLS-1$
	private static final String SCRIPT_FILE_NAME = "f1.js"; //$NON-NLS-1$
	private static final String STYLESHEET_FILE_NAME
			= "stylesRed.css"; //$NON-NLS-1$
	private static final String TEST_FILE_PATH
			= "JBIDE/5382/OpenOnJsf20.xhtml"; //$NON-NLS-1$
	private VpeController vpeController;
	private Document sourceDocument;

	public OpenOnJsf20Test_JBIDE5382(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vpeController = openInVpe(JsfAllTests.IMPORT_JSF_20_PROJECT_NAME,
				TEST_FILE_PATH);
		sourceDocument = getSourceDocument(vpeController);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// JUNIT TESTING METHODS

	public void testSourceOpenOnOutputStylesheet() throws Throwable {
		openOnSourceNode(getOutputStylesheetNode()
				.getAttributeNode(JSF.ATTR_NAME));
		assertActiveEditorInputNameEquals(STYLESHEET_FILE_NAME);
	}

	public void testSourceOpenOnOutputScript() throws Throwable {
		openOnSourceNode(getOutputScriptNode().getAttributeNode(JSF.ATTR_NAME));
		assertActiveEditorInputNameEquals(SCRIPT_FILE_NAME);
	}
	
	public void testVisualOpenOnOutputStylesheet() {
		showInvisibleTags();
		openOnCorrespondingVisualNode(getOutputStylesheetNode());
		assertActiveEditorInputNameEquals(STYLESHEET_FILE_NAME);
	}
	
	public void testVisualOpenOnOutputScript() {
		showInvisibleTags();
		openOnCorrespondingVisualNode(getOutputScriptNode());
		assertActiveEditorInputNameEquals(SCRIPT_FILE_NAME);
	}

	////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS

	private Element getOutputScriptNode() {
		return sourceDocument.getElementById(OUTPUT_SCRIPT_ELEMENT_ID);
	}
	
	private Element getOutputStylesheetNode() {
		return sourceDocument.getElementById(OUTPUT_STYLESHEET_ELEMENT_ID);
	}
	
	private void openOnSourceNode(Node sourceNode) throws Throwable {
		int offset = NodesManagingUtil.getStartOffsetNode(sourceNode);
		OpenOnUtil.performOpenOnAction(vpeController.getSourceEditor(), offset);
	}
	
	private void openOnCorrespondingVisualNode(Node sourceNode) {
		nsIDOMNode visualNode = vpeController.getDomMapping()
				.getNearVisualNode(sourceNode);
		vpeController.getSourceBuilder().openOn(visualNode);
	}
	
	private void showInvisibleTags() {
		vpeController.getVisualBuilder().setShowInvisibleTags(true);
		vpeController.visualRefresh();
		TestUtil.waitForIdle();
	}
	
	private void assertActiveEditorInputNameEquals(String expectedName) {
		IEditorPart activeEditorPart = (IEditorPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		String actualName = activeEditorPart.getEditorInput().getName();
		
		assertEquals("No file is opened or a wrong file "		//$NON-NLS-1$
						+ "is opened on the OpenOn action .",	//$NON-NLS-1$
		expectedName, actualName);		
	}
}
