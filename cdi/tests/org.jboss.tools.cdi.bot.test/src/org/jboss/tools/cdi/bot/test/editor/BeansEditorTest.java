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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.jboss.tools.cdi.bot.test.uiutils.editor.BeansEditor;
import org.jboss.tools.cdi.bot.test.uiutils.editor.BeansEditor.Item;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Lukas Jungmann
 */
@SWTBotTestRequires(clearProjects = false, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ BeansEditorTest.class })
public class BeansEditorTest extends SWTTestExt {

	private static final String descPath = "WebContent/WEB-INF/beans.xml";
	private static final String project = "CDIProject";
	private static final Logger LOGGER = Logger.getLogger(BeansEditorTest.class.getName());
	
	@BeforeClass
	public static void prepare() {
		copyResource("resources/beans.xml", descPath);
		copyResource("resources/Foo.jav_", "src/cdi/Foo.java");
		copyResource("resources/Bar.jav_", "src/cdi/Bar.java");
	}
	
	@Before
	public void setup() {
		new ProjectExplorer().openFile(project, descPath.split("/"));
	}
	
	@Test
	public void testInterceptors() {
		addItem(Item.INTERCEPTOR, "cdi.I1");
		removeItem(Item.INTERCEPTOR, "cdi.I1");
		addItem(Item.INTERCEPTOR, "cdi.I2");
	}

	@Test
	public void testDecorators() {
		addItem(Item.DECORATOR, "cdi.MapDecorator");
		addItem(Item.DECORATOR, "cdi.ComparableDecorator");
		removeItem(Item.DECORATOR, "cdi.ComparableDecorator");
	}
	
	@Test
	public void testClasses() {
		addItem(Item.CLASS, "cdi.Foo");
		addItem(Item.CLASS, "cdi.Bar");
		removeItem(Item.CLASS, "cdi.Foo");
	}
	
	@Test
	public void testStereotypes() {
		addItem(Item.STEREOTYPE, "cdi.S2");
		addItem(Item.STEREOTYPE, "cdi.S3");
		removeItem(Item.STEREOTYPE, "cdi.S3");
		addItem(Item.STEREOTYPE, "cdi.S1");
		removeItem(Item.STEREOTYPE, "cdi.S2");
	}
	
	@Test
	public void testResult() {
		SWTBotEditor editor = new SWTWorkbenchBot().activeEditor();
		BeansEditor be = new BeansEditor(editor.getReference(), new SWTWorkbenchBot());
		be.activatePage("Source");
		String text = be.toTextEditor().getText();
		List<Node> nl = getItems(text, Item.INTERCEPTOR);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, "cdi.I2"));

		nl = getItems(text, Item.DECORATOR);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, "cdi.MapDecorator"));

		nl = getItems(text, Item.CLASS);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, "cdi.Bar"));

		nl = getItems(text, Item.STEREOTYPE);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, "cdi.S1"));
	}
	
	private void addItem(Item item, String name) {
		SWTBotEditor editor = new SWTWorkbenchBot().activeEditor();
		BeansEditor be = new BeansEditor(editor.getReference(), new SWTWorkbenchBot());
		be.activatePage("Tree");
		try {
			be.add(item, name);
			Assert.assertTrue(be.isDirty());
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
			if (item == Item.STEREOTYPE) {
				if ("stereotype".equals(n.getNodeName())) {
					list.add(n);
				}
			} else {
				if ("class".equals(n.getNodeName())) {
					list.add(n);
				}
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
	
	private static void copyResource(String src, String target) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProjects()[0];
		IFile f = project.getFile(target);
		if (f.exists()) {
			LOGGER.info("Replacing " + target + " file");
			try {
				f.delete(true, new NullProgressMonitor());
			} catch (CoreException ce) {
				LOGGER.log(Level.WARNING, ce.getMessage(), ce);
			}
		}
		InputStream is = null;
		try {
			is = BeansEditorTest.class.getResourceAsStream(src);
			f.create(is, true, new NullProgressMonitor());
		} catch (CoreException ce) {
			LOGGER.log(Level.WARNING, ce.getMessage(), ce);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					//ignore
				}
			}
		}
	}
}
