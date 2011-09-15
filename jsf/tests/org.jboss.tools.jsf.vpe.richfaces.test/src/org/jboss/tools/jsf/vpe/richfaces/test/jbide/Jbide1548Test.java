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

package org.jboss.tools.jsf.vpe.richfaces.test.jbide;

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Class for testing all jsf bugs
 * 
 * @author sdzmitrovich
 * 
 * test for http://jira.jboss.com/jira/browse/JBIDE-1548
 * 
 * 
 */
public class Jbide1548Test extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/1548/JBIDE-1548.xhtml";

	// type of input tag

	// import project name
	public Jbide1548Test(String name) {
		super(name);
	}

	/*
	 * JBIDE's test cases
	 */

	public void testJbide() throws Throwable {

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				TEST_PAGE_NAME, RichFacesAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME
				+ ";projectName = " + RichFacesAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();

		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "table" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

		assertEquals(2, elements.size());

		// get first table. It has default attributes (minValue=0, maxValue=100)
		nsIDOMNode defaultInputSlider = elements.get(0);
		// check min-,max- values
		checkMinMaxValue(defaultInputSlider, 0, 100);

		// get first table. It has edited attributes (minValue=-10,
		// maxValue=200)
		nsIDOMNode editedInputSlider = elements.get(1);
		// check min-,max- values
		checkMinMaxValue(editedInputSlider, -10, 200);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * check minValue and maxValue
	 * 
	 * inputNumberSlider has next structure
	 * 
	 * <code>
	 *		<table>
	 * 			<tr>
	 * 				<td>minValue</td>
	 * 				<td>maxValue</td>
	 * 			</tr>
	 *  		.... 
	 *  	</table>
	 *  </code>
	 * 
	 */
	private void checkMinMaxValue(nsIDOMNode defaultInputSlider,
			int expectedMinValue, int expectedMaxValue) {

		// get "tr" element
		nsIDOMNode trNode = defaultInputSlider.getChildNodes().item(0);
		assertNotNull(trNode);
		// get first "td" element which contain minValue
		nsIDOMNode td1Node = trNode.getChildNodes().item(0);
		assertNotNull(td1Node);

		// get second "td" element which contain maxValue
		nsIDOMNode td2Node = trNode.getChildNodes().item(1);
		assertNotNull(td2Node);

		// get minValue
		nsIDOMNode minValue = td1Node.getChildNodes().item(0);
		assertNotNull(minValue);

		// get maxValue
		nsIDOMNode maxValue = td2Node.getChildNodes().item(0);
		assertNotNull(maxValue);

		// check min value
		String minValueString = minValue.getNodeValue();
		assertNotNull(minValueString);
		assertEquals(expectedMinValue, Integer.parseInt(minValueString));

		// check max value
		String maxValueString = maxValue.getNodeValue();
		assertNotNull(maxValueString);
		assertEquals(expectedMaxValue, Integer.parseInt(maxValueString));

		return;
	}

}
