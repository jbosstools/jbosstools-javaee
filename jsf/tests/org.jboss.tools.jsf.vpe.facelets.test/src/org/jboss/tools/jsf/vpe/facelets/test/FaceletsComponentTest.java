/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.facelets.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMText;

/**
 * Class for testing all Facelets components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class FaceletsComponentTest extends VpeTest {

    // import project name
    public static final String IMPORT_PROJECT_NAME = "faceletsTest";

    public FaceletsComponentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    /**
     * Test for ui:debug
     * 
     * @throws Throwable
     */
    public void testDebug() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/debug.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    /**
     * Test for ui:define
     * 
     * @throws Throwable
     */
    public void testDefine() throws Throwable {

	nsIDOMElement element = performTestForFaceletComponent("components/define.xhtml");
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "span" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_SPAN);

	assertEquals(2, elements.size());
	nsIDOMElement elementSpan0 = (nsIDOMElement) elements.get(0)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	nsIDOMElement elementSpan1 = (nsIDOMElement) elements.get(1)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	nsIDOMText text0 = (nsIDOMText) elementSpan0.getFirstChild()
		.queryInterface(nsIDOMText.NS_IDOMTEXT_IID);
	nsIDOMText text1 = (nsIDOMText) elementSpan1.getFirstChild()
		.queryInterface(nsIDOMText.NS_IDOMTEXT_IID);

	assertEquals(text0.getNodeValue(), "Greeting Page");
	assertEquals(text1.getNodeValue(), "#{person.name}!");

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:composition
     * 
     * @throws Throwable
     */
    public void testComposition() throws Throwable {

	// check absolute path
	nsIDOMElement element = performTestForFaceletComponent("components/composition_absolute.xhtml");
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "table" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

	assertEquals(1, elements.size());

	// check related path
	element = performTestForFaceletComponent("components/composition_related.xhtml");
	node = (nsIDOMNode) element.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	elements = new ArrayList<nsIDOMNode>();

	// find "table" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

	assertEquals(1, elements.size());

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:component
     * 
     * @throws Throwable
     */
    public void testComponent() throws Throwable {

	nsIDOMElement element = performTestForFaceletComponent("components/component.xhtml");
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "div" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_DIV);
	assertEquals(1, elements.size());
	nsIDOMElement divElement = (nsIDOMElement) elements.get(0)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	nsIDOMElement divBody = (nsIDOMElement) divElement.getLastChild()
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	nsIDOMElement div = (nsIDOMElement) divBody.getLastChild()
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	String title = div.getAttribute("title");

	assertEquals(title.replaceAll("\\s+", ""),
		"ui:componentbinding:#{backingBean.menu}");
	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:remove
     * 
     * @throws Throwable
     */
    public void testRemove() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/remove.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	// TODO check that content in ui:remove isn't shown in VPE
	assertTrue("Content inside ui:remove tag shouldn't be shown", false);
    }

    /**
     * Test for ui:decorate
     * 
     * @throws Throwable
     */
    public void testDecorate() throws Throwable {
	// check absolute path
	nsIDOMElement element = performTestForFaceletComponent("components/decorate_absolute.xhtml");
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "table" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

	assertEquals(1, elements.size());

	// check related path
	element = performTestForFaceletComponent("components/decorate_related.xhtml");
	node = (nsIDOMNode) element.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	elements = new ArrayList<nsIDOMNode>();

	// find "table" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

	assertEquals(1, elements.size());

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:repeat
     * 
     * @throws Throwable
     */
    public void testRepeat() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/repeat.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	assertTrue("Component's content is not shown", false);
    }

    /**
     * Test for ui:include
     * 
     * @throws Throwable
     */
    public void testInclude() throws Throwable {
	// check absolute path
	nsIDOMElement element = performTestForFaceletComponent("components/include_absolute.xhtml");
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "table" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

	assertEquals(1, elements.size());

	// check related path
	element = performTestForFaceletComponent("components/include_related.xhtml");
	node = (nsIDOMNode) element.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	elements = new ArrayList<nsIDOMNode>();

	// find "table" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

	assertEquals(1, elements.size());

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:fragment
     * 
     * @throws Throwable
     */
    public void testFragment() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/fragment.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	// TODO check that fragment's content is showed
	assertTrue("Fragment's content is not sown", false);
    }

    /**
     * Test for ui:insert
     * 
     * @throws Throwable
     */
    public void testInsert() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/insert.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    /**
     * 
     * @param componentPage
     * @return
     * @throws Throwable
     */
    private nsIDOMElement performTestForFaceletComponent(String componentPage)
	    throws Throwable {
	TestUtil.waitForJobs();
	// set exception
	setException(null);

	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(componentPage,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + file.getFullPath(),
		file);

	IEditorInput input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);
	// open and get editor
	JSPMultiPageEditor part = openEditor(input);

	// get dom document
	nsIDOMDocument document = getVpeVisualDocument(part);
	nsIDOMElement element = document.getDocumentElement();

	// check that element is not null
	assertNotNull(element);

	return element;
    }

}
