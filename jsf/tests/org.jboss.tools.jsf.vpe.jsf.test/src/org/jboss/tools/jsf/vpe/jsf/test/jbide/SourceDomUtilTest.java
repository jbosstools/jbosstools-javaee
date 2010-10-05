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
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.text.MessageFormat;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Tests for {@link SourceDomUtil} class. 
 * 
 * @author Yahor Radtsevich (yradtsevich)
 * @see <a href="https://jira.jboss.org/browse/JBIDE-7209">JBIDE-7209</a>
 */
public class SourceDomUtilTest extends VpeTest {
	private static final String TEST_FILE_PATH
			= "JBIDE/7209/getNodeByXPath.html"; //$NON-NLS-1$
	private static final String ID_1 = "id1"; //$NON-NLS-1$
	private static final String ID_2 = "id2"; //$NON-NLS-1$
	private static final String X_PATH_3 = "/html/body/table/tr/td[1]/@onclick";  //$NON-NLS-1$
	private static final String VALUE_3 = "f()";  //$NON-NLS-1$

	private VpeController vpeController;

	public SourceDomUtilTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				TEST_FILE_PATH);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// JUNIT TESTING METHODS
	/**
	 * Test for {@link SourceDomUtil#getXPath(Node)} and
	 * {@link SourceDomUtil#getNodeByXPath(Document, String)}.
	 * 
	 * @see <a href="https://jira.jboss.org/browse/JBIDE-7209">JBIDE-7209</a>
	 */
	public void testGetNodeByXPath() {
		 Document document = vpeController.getPageContext()
		 		.getSourceBuilder().getSourceDocument();
		 
		 Node node1 = document.getElementById(ID_1);
		 assertNotNull(node1);
		 String xPath1 = SourceDomUtil.getXPath(node1);
		 assertEquals(MessageFormat.format(
				"getNodeByXPath is not inverse of getXPath for {0}", ID_1), //$NON-NLS-1$
				 node1, SourceDomUtil.getNodeByXPath(document, xPath1));
		 
		 Node node2 = document.getElementById(ID_2);
		 assertNotNull(node2);
		 String xPath2 = SourceDomUtil.getXPath(node2);
		 assertEquals(MessageFormat.format(
					"getNodeByXPath is not inverse of getXPath for {0}", ID_2), //$NON-NLS-1$
				 node2, SourceDomUtil.getNodeByXPath(document, xPath2));
		 
		 Node node3 = SourceDomUtil.getNodeByXPath(document, X_PATH_3);
		 assertTrue(MessageFormat.format("{0} is not an attribute", X_PATH_3), //$NON-NLS-1$
				 node3 instanceof Attr);
		 assertEquals(MessageFormat.format(
				 "Value of {0} is not equal to {1}", X_PATH_3, VALUE_3), //$NON-NLS-1$
				 VALUE_3, ((Attr)node3).getValue());
	}
}
