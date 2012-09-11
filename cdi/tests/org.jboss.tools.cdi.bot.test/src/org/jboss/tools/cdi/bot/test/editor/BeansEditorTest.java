/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.

 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.editor.BeansEditor.Item;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Test checks if Bean Editor works properly
 * prerequisite - CDIAtWizardTest!!!
 * 
 * 
 * @author Lukas Jungmann
 * @author jjankovi
 */
@Require(clearProjects = false, perspective = "Java EE", 
server = @Server(state = ServerState.NotRunning, 
version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class, CDISmokeBotTests.class })
public class BeansEditorTest extends CDITestBase {

	private static final String descPath = CDIConstants.WEB_INF_BEANS_XML_PATH;
		
	@BeforeClass
	public static void setup() {
		editResourceUtil.copyResource("resources/beans.xml", descPath);
		editResourceUtil.copyResource("resources/Foo.jav_", "src/cdi/Foo.java");
		editResourceUtil.copyResource("resources/Bar.jav_", "src/cdi/Bar.java");
		/**
		 * project should be located in workspace after previous test
		 */
		try {
			projectExplorer.selectProject("CDIProject");
		} catch (WidgetNotFoundException wnfe) {
			fail("project should be located in workspace");
		}
	}
	
	@Override
	public void prepareWorkspace() {
		new ProjectExplorer().openFile(getProjectName(), descPath.split("/"));								
	}
			
	@Test
	public void testClasses() {		
		addItem(Item.CLASS, getPackageName() + ".Foo");
		addItem(Item.CLASS, getPackageName() + ".Bar");
		removeItem(Item.CLASS, getPackageName() + ".Foo");
	}
	
	@Test
	public void testInterceptors() {
		addItem(Item.INTERCEPTOR, getPackageName() + ".I1");
		removeItem(Item.INTERCEPTOR, getPackageName() + ".I1");
		addItem(Item.INTERCEPTOR, getPackageName() + ".I2");
	}

	
	@Test
	public void testDecorators() {
		addItem(Item.DECORATOR, getPackageName() + ".MapDecorator");
		addItem(Item.DECORATOR, getPackageName() + ".ComparableDecorator");
		removeItem(Item.DECORATOR, getPackageName() + ".ComparableDecorator");
	}
		
	
	@Test
	public void testStereotypes() {
		addItem(Item.STEREOTYPE, getPackageName() + ".S2");
		addItem(Item.STEREOTYPE, getPackageName() + ".S3");
		removeItem(Item.STEREOTYPE, getPackageName() + ".S3");
		addItem(Item.STEREOTYPE, getPackageName() + ".S1");
		removeItem(Item.STEREOTYPE, getPackageName() + ".S2");
	}
	
	@Test
	public void testResult() {
		SWTBotEditor editor = new SWTWorkbenchBot().activeEditor();
		BeansEditor be = new BeansEditor(editor.getReference(), new SWTWorkbenchBot());
		be.activatePage("Source");
		String text = be.toTextEditor().getText();
		List<Node> nl = getItems(text, Item.INTERCEPTOR);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, getPackageName() + ".I2"));

		nl = getItems(text, Item.DECORATOR);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, getPackageName() + ".MapDecorator"));

		nl = getItems(text, Item.CLASS);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, getPackageName() + ".Bar"));

		nl = getItems(text, Item.STEREOTYPE);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, getPackageName() + ".S1"));
	}
	
	private void addItem(Item item, String name) {
		SWTBotEditor editor = new SWTWorkbenchBot().activeEditor();
		BeansEditor be = new BeansEditor(editor.getReference(), new SWTWorkbenchBot());
		be.activatePage("Tree");
		try {
			be.add(item, name);
			Assert.assertTrue(be.isDirty());
			Assert.assertEquals(name, be.getSelectedItem());
			be.activatePage("Source");
			String text = be.toTextEditor().getText();
			List<Node> nl = getItems(text, item);
			assertTrue(containsItem(nl, name));
		} finally {
			if (be.isDirty()) {
				be.save();
			}
		}
	}
	
	private void removeItem(Item item, String name) {
		SWTBotEditor editor = new SWTWorkbenchBot().activeEditor();
		BeansEditor be = new BeansEditor(editor.getReference(), new SWTWorkbenchBot());
		be.activatePage("Tree");
		try {
			be.remove(item, name);
			Assert.assertTrue(be.isDirty());
			be.activatePage("Source");
			String text = be.toTextEditor().getText();
			List<Node> nl = getItems(text, item);
			assertFalse(containsItem(nl, name));
		} finally {
			if (be.isDirty()) {
				be.save();
			}
		}
	}
	
	private Document getDocument(String text) {
		Document d = null;
		try {
			d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(text.getBytes()));
		} catch (SAXException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return d;
	}
	
	private List<Node> getItems(String doc, Item i) {
		Document d = getDocument(doc);
		NodeList nl = null;
		switch (i) {
		case DECORATOR:
			nl = d.getElementsByTagName("decorators");
			break;
		case INTERCEPTOR:
			nl = d.getElementsByTagName("interceptors");
			break;
		case STEREOTYPE:
			return getNodes(d.getElementsByTagName("stereotype"), i);
		case CLASS:
			nl = d.getElementsByTagName("alternatives");
			break;
		}
		return nl.getLength() > 0 ? getNodes(nl.item(0).getChildNodes(), i) : new ArrayList<Node>();
	}
	
	private List<Node> getNodes(NodeList nl, Item item) {
		List<Node> list = new ArrayList<Node>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (item.getElementName().equals(n.getNodeName())) {
				list.add(n);
			}
		}
		return list;
	}

	private boolean containsItem(List<Node> nl, String name) {
		if (nl == null) {
			return false;
		}
		for (int i = 0; i < nl.size(); i++) {
			if (name.equals(nl.get(i).getTextContent())) {
				return true;
			}
		}
		return false;
	}
	
}
