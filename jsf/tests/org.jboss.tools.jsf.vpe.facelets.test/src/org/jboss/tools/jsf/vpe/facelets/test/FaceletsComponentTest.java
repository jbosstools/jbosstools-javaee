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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
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

    private static final String PAGE_HEADER = "Page Header"; //$NON-NLS-1$
    private static final String USER = "#{user}"; //$NON-NLS-1$
    private static final String COMPONENT_S_CONTENT_IS_NOT_SHOWN = "Component's content is not shown"; //$NON-NLS-1$
    private static final String TEMPLATE_WITH_RELATED_PATH_IS_NOT_INCLUDED = "Template with related path is not included"; //$NON-NLS-1$
    private static final String TEMPLATE_WITH_ABSOLUTE_PATH_IS_NOT_INCLUDED = "Template with absolute path is not included"; //$NON-NLS-1$
    private static final String DEFINED_CONTENT_IS_NOT_SHOWN = "Defined content is not shown"; //$NON-NLS-1$

    public FaceletsComponentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    /**
     * Test for ui:debug
     * 
     * @throws Throwable
     */
    public void _testDebug() throws Throwable {
	nsIDOMElement element = performTestForFaceletComponent("components/debug.xhtml"); //$NON-NLS-1$
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "div" elements
	TestUtil.findAllElementsByName(node, elements, HTML.TAG_DIV);
	assertEquals(5, elements.size());

	nsIDOMElement divElement = queryInterface(elements.get(4), nsIDOMElement.class);

	nsIDOMText text = queryInterface(divElement.getFirstChild(), nsIDOMText.class);

	assertEquals("Debug's content is not shown", text.getNodeValue(), //$NON-NLS-1$
		"Ctrl+Shift+"); //$NON-NLS-1$

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:define
     * 
     * @throws Throwable
     */
    public void testDefine() throws Throwable {

	nsIDOMElement element = performTestForFaceletComponent("components/define.xhtml"); //$NON-NLS-1$
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "span" elements
	TestUtil.findAllElementsByName(node, elements, HTML.TAG_SPAN);

	assertEquals(3, elements.size());

	nsIDOMElement elementSpan0 = queryInterface(elements.get(0), nsIDOMElement.class);
	nsIDOMElement elementSpan1 = queryInterface(elements.get(1), nsIDOMElement.class);
	nsIDOMElement elementSpan2 = queryInterface(elements.get(2), nsIDOMElement.class);

	nsIDOMText text0 = queryInterface(elementSpan0.getFirstChild(), nsIDOMText.class);
	nsIDOMText text1 = queryInterface(elementSpan1.getFirstChild(), nsIDOMText.class);
	nsIDOMText text2 = queryInterface(elementSpan2.getFirstChild(), nsIDOMText.class);

	assertEquals(DEFINED_CONTENT_IS_NOT_SHOWN, text0.getNodeValue(),
		"Greeting Page"); //$NON-NLS-1$
	assertEquals(DEFINED_CONTENT_IS_NOT_SHOWN, text1.getNodeValue(), 
		USER);
	assertEquals(DEFINED_CONTENT_IS_NOT_SHOWN, text2.getNodeValue(),
		"#{person.name}!"); //$NON-NLS-1$

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
	nsIDOMElement element = performTestForFaceletComponent("components/composition_absolute.xhtml"); //$NON-NLS-1$

	checkTemplatePage(element, PAGE_HEADER,
		TEMPLATE_WITH_ABSOLUTE_PATH_IS_NOT_INCLUDED);

	// check related path
	element = performTestForFaceletComponent("components/composition_related.xhtml"); //$NON-NLS-1$

	checkTemplatePage(element, PAGE_HEADER,
		TEMPLATE_WITH_RELATED_PATH_IS_NOT_INCLUDED);

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

	nsIDOMElement element = performTestForFaceletComponent("components/component.xhtml"); //$NON-NLS-1$
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "div" elements
	TestUtil.findAllElementsByName(node, elements, HTML.TAG_DIV);

	assertEquals(1, elements.size());

	nsIDOMElement div = queryInterface(elements.get(0), nsIDOMElement.class);

	String title = div.getAttribute("title"); //$NON-NLS-1$

	assertEquals(COMPONENT_S_CONTENT_IS_NOT_SHOWN, title.replaceAll("\\s+", //$NON-NLS-1$
		""), "ui:componentbinding:#{backingBean.menu}"); //$NON-NLS-1$ //$NON-NLS-2$
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
	nsIDOMElement element = performTestForFaceletComponent("components/remove.xhtml"); //$NON-NLS-1$
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "span" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_SPAN);

	for (int i = 0; i < elements.size(); i++) {

	    nsIDOMElement elementSpan = queryInterface(elements.get(i), nsIDOMElement.class);

	    nsIDOMText text = queryInterface(elementSpan.getFirstChild(), nsIDOMText.class);
	    if (text == null)
		continue;
	    assertEquals(
		    "Content inside ui:remove tag shouldn't be shown", false, //$NON-NLS-1$
		    text.getNodeValue().equals("\nThis will be removed.\n")); //$NON-NLS-1$
	}
	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:decorate
     * 
     * @throws Throwable
     */
    public void testDecorate() throws Throwable {
	// check absolute path
	nsIDOMElement element = performTestForFaceletComponent("components/decorate_absolute.xhtml"); //$NON-NLS-1$

	checkTemplatePage(element, PAGE_HEADER,
		TEMPLATE_WITH_ABSOLUTE_PATH_IS_NOT_INCLUDED);

	// check related path
	element = performTestForFaceletComponent("components/decorate_related.xhtml"); //$NON-NLS-1$

	checkTemplatePage(element, PAGE_HEADER,
		TEMPLATE_WITH_RELATED_PATH_IS_NOT_INCLUDED);

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
	nsIDOMElement element = performTestForFaceletComponent("components/repeat.xhtml"); //$NON-NLS-1$
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "dl" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_DL);

	assertEquals(1, elements.size());

	nsIDOMElement elementDL = queryInterface(elements.get(0), nsIDOMElement.class);

	elements.clear();

	TestUtil.findAllElementsByName(elementDL, elements, HTML.TAG_DT);

	assertEquals(1, elements.size());

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:include
     * 
     * @throws Throwable
     */
    public void testInclude() throws Throwable {
	// check absolute path
	nsIDOMElement element = performTestForFaceletComponent("components/include_absolute.xhtml"); //$NON-NLS-1$

	checkTemplatePage(element, PAGE_HEADER,
		TEMPLATE_WITH_ABSOLUTE_PATH_IS_NOT_INCLUDED);

	// check related path
	element = performTestForFaceletComponent("components/include_related.xhtml"); //$NON-NLS-1$

	checkTemplatePage(element, PAGE_HEADER,
		TEMPLATE_WITH_RELATED_PATH_IS_NOT_INCLUDED);

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
	nsIDOMElement element = performTestForFaceletComponent("components/fragment.xhtml"); //$NON-NLS-1$
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "div" elements
	TestUtil.findAllElementsByName(node, elements, HTML.TAG_DIV);
	assertEquals(4, elements.size());

	nsIDOMElement div = queryInterface(elements.get(3), nsIDOMElement.class);

	String title = div.getAttribute("title"); //$NON-NLS-1$

	assertEquals("Fragment's content is not shown", title.replaceAll( //$NON-NLS-1$
		"\\s+", ""), "ui:fragmentbinding:#{uiCache['searchResult']}"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Test for ui:insert
     * 
     * @throws Throwable
     */
    public void testInsert() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/insert.xhtml", FaceletsAllTests.IMPORT_PROJECT_NAME));  //$NON-NLS-1$
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
		FaceletsAllTests.IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file. componentPage = " + componentPage//$NON-NLS-1$ 
			+ ";projectName = " + FaceletsAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

	IEditorInput input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input); //$NON-NLS-1$
	// open and get editor
	JSPMultiPageEditor part = openEditor(input);

	// get dom document
	nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
	nsIDOMElement element = document.getDocumentElement();

	// check that element is not null
	assertNotNull(element);

	return element;
    }

    /**
     * Test for ui:param
     * 
     * @throws Throwable
     */
    public void testParam() throws Throwable {
	// check absolute path
	nsIDOMElement element = performTestForFaceletComponent("components/param.xhtml"); //$NON-NLS-1$

	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "td" elements
	TestUtil.findAllElementsByName(node, elements, HTML.TAG_TD);
	assertEquals(5, elements.size());
	nsIDOMElement td = queryInterface(elements.get(1), nsIDOMElement.class);
	nsIDOMElement span = queryInterface(td.getFirstChild(), nsIDOMElement.class);

	nsIDOMText text = queryInterface(span.getFirstChild(), nsIDOMText.class);

	assertEquals(COMPONENT_S_CONTENT_IS_NOT_SHOWN, text.getNodeValue(),
		USER);

	if (getException() != null) {
	    throw getException();
	}
    }

    private void checkTemplatePage(nsIDOMElement element, String contextString,
	    String message) {
	nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "td" elements
	TestUtil.findAllElementsByName(node, elements, HTML.TAG_TD);
	assertEquals(message, 5, elements.size());
	nsIDOMElement td = queryInterface(elements.get(0), nsIDOMElement.class);
	nsIDOMElement div = queryInterface(td.getFirstChild(), nsIDOMElement.class);
	nsIDOMElement span = queryInterface(div.getFirstChild(), nsIDOMElement.class);

	nsIDOMText text = queryInterface(span.getFirstChild(), nsIDOMText.class);

	assertEquals(message, contextString, text.getNodeValue());
    }

    /**
     * Test for all facelets tags
     * 
     * @throws Throwable
     */
    public void testAllTags() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/faceletsTest.xhtml", FaceletsAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

}
