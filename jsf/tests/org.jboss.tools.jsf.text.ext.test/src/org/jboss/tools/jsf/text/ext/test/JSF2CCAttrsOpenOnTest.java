/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSF2CCAttrsOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME =  PROJECT_NAME+"/WebContent/resources/demo/input.xhtml";
	private static final String PAGE2_NAME =  PROJECT_NAME+"/WebContent/resources/demo/input2.xhtml";
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		JobUtils.waitForIdle();
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2CCAttrsOpenOnTest() {
		super("JSF2 OpenOn on CC Attributes test");
	}

	static String COMPOSITE_URI = "http://java.sun.com/jsf/composite";
	static String COMPOSITE_INTERFACE_NODE = ":interface";
	static String COMPOSITE_ATTRIBUTE_NODE = ":attribute";
	static String COMPOSITE_NAME_ATTRIBUTE = "name";
	

	public void testCCInterface() throws PartInitException, BadLocationException {
		final String editorName = "input.xhtml";
		final String elToTest = "cc.attrs";  
		IEditorPart editor = WorkbenchUtils.openEditor(PAGE_NAME);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JobUtils.waitForIdle();
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
			
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				elToTest, true, true, false, false);
		
		assertNotNull("CC Interface reference: "+elToTest+" not found",reg);
		
		IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true); // new Region(reg.getOffset() + reg.getLength(), 0)
		
		assertNotNull("Hyperlinks for CC Interface :"+elToTest+" are not found",links);
		
		assertTrue("Hyperlinks for CC Interface: "+elToTest+" are not found",links.length!=0);
		
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			JobUtils.waitForIdle(2000);
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorName.equals(resultEditor.getTitle())){

				assertTrue("Opened editor is not JSPMultiPageEditor: "+resultEditor.getClass().getName(), (resultEditor instanceof JSPMultiPageEditor));

				JSPMultiPageEditor multyPageEditor = (JSPMultiPageEditor) resultEditor;
				viewer = multyPageEditor.getSourceEditor().getTextViewer(); 
				assertNotNull("An error occured while trying to access the editor's viewer!", viewer);
				document = viewer.getDocument();
				assertNotNull("An error occured while trying to access the viewer's document!", document);
				Point sel = viewer.getSelectedRange();
				assertNotNull("An error occured while trying to access the viewer's selection!", viewer);
				
				StructuredModelWrapper smw = new StructuredModelWrapper();
				smw.init(document);
				try {
					Document xmlDocument = smw.getDocument();
					assertNotNull("An error occured while trying to access the structured document!", xmlDocument);
					
					Node node = Utils.findNodeForOffset(xmlDocument, sel.x);
					assertNotNull("The document selection is not a CC Interface!", node);

					assertTrue("The document selection is not a CC Interface!", COMPOSITE_URI.equals(node.getNamespaceURI()));
					assertTrue("The document selection is not a CC Interface!", node.getNodeName().endsWith(COMPOSITE_INTERFACE_NODE));
					return;
				} finally {
					smw.dispose();
				}
			}
		}
	}

	/**
	 * This test runs with default root element <html> and default namespace prefix 'composite'.
	 */
	public void testCCInterfaceAttrs1() throws PartInitException, BadLocationException {
		testCCInterfaceAttrs(PAGE_NAME, "input.xhtml");
	}

	/**
	 * In input2.xhtml root element is not html and namespace prefix is 'cc' instead of default 'composite'.
	 */
	public void testCCInterfaceAttrs2() throws PartInitException, BadLocationException {
		testCCInterfaceAttrs(PAGE2_NAME, "input2.xhtml");
	}

	void testCCInterfaceAttrs(String pageName, String editorName) throws PartInitException, BadLocationException {
		final String elToTest = "cc.attrs.action";
		final String atributeAction = "action";
		IEditorPart editor = WorkbenchUtils.openEditor(pageName);
		assertTrue(editor instanceof JSPMultiPageEditor);
		JobUtils.waitForIdle();
		JSPMultiPageEditor jspMultyPageEditor = (JSPMultiPageEditor) editor;
		ISourceViewer viewer = jspMultyPageEditor.getSourceEditor().getTextViewer(); 
			
		IDocument document = viewer.getDocument();
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
				elToTest, true, true, false, false);
		assertNotNull("CC Interface reference: "+elToTest+" not found",reg);

		reg = new FindReplaceDocumentAdapter(document).find(reg.getOffset(),
				atributeAction, true, true, false, false);
		assertNotNull("CC Interface reference: "+elToTest+" not found",reg);
		
		
		IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, reg, true); // new Region(reg.getOffset() + reg.getLength(), 0)
		
		assertNotNull("Hyperlinks for CC Interface :"+elToTest+" are not found",links);
		
		assertTrue("Hyperlinks for CC Interface: "+elToTest+" are not found",links.length!=0);
		
		for(IHyperlink link : links){
			assertNotNull(link.toString());
			
			link.open();
			JobUtils.waitForIdle(2000);
			
			IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorName.equals(resultEditor.getTitle())){

				assertTrue("Opened editor is not JSPMultiPageEditor: "+resultEditor.getClass().getName(), (resultEditor instanceof JSPMultiPageEditor));

				JSPMultiPageEditor multyPageEditor = (JSPMultiPageEditor) resultEditor;
				viewer = multyPageEditor.getSourceEditor().getTextViewer(); 
				assertNotNull("An error occured while trying to access the editor's viewer!", viewer);
				document = viewer.getDocument();
				assertNotNull("An error occured while trying to access the viewer's document!", document);
				Point sel = viewer.getSelectedRange();
				assertNotNull("An error occured while trying to access the viewer's selection!", viewer);
				
				StructuredModelWrapper smw = new StructuredModelWrapper();
				smw.init(document);
				try {
					Document xmlDocument = smw.getDocument();
					assertNotNull("An error occured while trying to access the structured document!", xmlDocument);
					
					Node node = Utils.findNodeForOffset(xmlDocument, sel.x);
					assertNotNull("The document selection is not a CC Interface!", node);

					assertTrue("The document selection is not a CC Interface!", COMPOSITE_URI.equals(node.getNamespaceURI()));
					assertTrue("The document selection is not a CC Interface!", node.getNodeName().endsWith(COMPOSITE_ATTRIBUTE_NODE));
					assertTrue("The document selection points to wrong CC Interface Attribute!", atributeAction.equals(((Element)node).getAttribute(COMPOSITE_NAME_ATTRIBUTE)));
					return;
				} finally {
					smw.dispose();
				}
			}
		}
	}
}

