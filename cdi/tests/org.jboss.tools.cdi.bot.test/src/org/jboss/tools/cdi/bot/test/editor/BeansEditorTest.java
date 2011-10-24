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
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.editor.BeansEditor;
import org.jboss.tools.cdi.bot.test.uiutils.editor.BeansEditor.Item;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.After;
import org.junit.AfterClass;
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
 * prerequisite - CDIAtWizardTest
 * 
 * 
 * TO DO - copy resources into right location - PACKAGE_NAME
 * 
 * @author Lukas Jungmann
 * @author jjankovi
 */
@Require(clearProjects = false, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class BeansEditorTest extends CDIBase {

	private static final String descPath = "WebContent/WEB-INF/beans.xml";
	private static final String project = "CDIProject";
	private static final String PACKAGE_NAME = "cdi";
	private static final Logger LOGGER = Logger.getLogger(BeansEditorTest.class.getName());
	
	@BeforeClass
	public static void prepare() {
		if (!projectExists(project)) {
			createAndCheckCDIProject(bot, util, projectExplorer, project);	
			createPackage(PACKAGE_NAME);
		}
						
		copyResource("resources/beans.xml", descPath);
		copyResource("resources/Foo.jav_", "src/" + PACKAGE_NAME + "/Foo.java");
		copyResource("resources/Bar.jav_", "src/" + PACKAGE_NAME + "/Bar.java");		
	}
		
	@AfterClass
	public static void clean() {		
		removeObjectInProjectExplorer(PACKAGE_NAME, project + "/Java Resources/src");
		removeObjectInProjectExplorer("beans.xml", project + "/WebContent/WEB-INF");
	}
	
	@Before
	public void setup() {
		new ProjectExplorer().openFile(project, descPath.split("/"));
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
		
	@Test
	public void testClasses() {		
		addItem(Item.CLASS, PACKAGE_NAME + ".Foo");
		addItem(Item.CLASS, PACKAGE_NAME + ".Bar");
		removeItem(Item.CLASS, PACKAGE_NAME + ".Foo");
	}
	
	@Test
	public void testInterceptors() {
		addItem(Item.INTERCEPTOR, PACKAGE_NAME + ".I1");
		removeItem(Item.INTERCEPTOR, PACKAGE_NAME + ".I1");
		addItem(Item.INTERCEPTOR, PACKAGE_NAME + ".I2");
	}

	
	@Test
	public void testDecorators() {
		addItem(Item.DECORATOR, PACKAGE_NAME + ".MapDecorator");
		addItem(Item.DECORATOR, PACKAGE_NAME + ".ComparableDecorator");
		removeItem(Item.DECORATOR, PACKAGE_NAME + ".ComparableDecorator");
	}
		
	
	@Test
	public void testStereotypes() {
		addItem(Item.STEREOTYPE, PACKAGE_NAME + ".S2");
		addItem(Item.STEREOTYPE, PACKAGE_NAME + ".S3");
		removeItem(Item.STEREOTYPE, PACKAGE_NAME + ".S3");
		addItem(Item.STEREOTYPE, PACKAGE_NAME + ".S1");
		removeItem(Item.STEREOTYPE, PACKAGE_NAME + ".S2");
	}
	
	@Test
	public void testResult() {
		SWTBotEditor editor = new SWTWorkbenchBot().activeEditor();
		BeansEditor be = new BeansEditor(editor.getReference(), new SWTWorkbenchBot());
		be.activatePage("Source");
		String text = be.toTextEditor().getText();
		List<Node> nl = getItems(text, Item.INTERCEPTOR);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, PACKAGE_NAME + ".I2"));

		nl = getItems(text, Item.DECORATOR);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, PACKAGE_NAME + ".MapDecorator"));

		nl = getItems(text, Item.CLASS);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, PACKAGE_NAME + ".Bar"));

		nl = getItems(text, Item.STEREOTYPE);
		assertEquals(1, nl.size());
		assertTrue(containsItem(nl, PACKAGE_NAME + ".S1"));
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
	
	private static void createPackage(String packageName) {
		projectExplorer.selectProject(project);
		eclipse.createNew(EntityType.JAVA_PACKAGE);
		SWTBot packageDialogBot = bot.activeShell().bot();
		packageDialogBot.textWithLabel("Name:").typeText(packageName);
		packageDialogBot.button("Finish").click();
		util.waitForNonIgnoredJobs();
		LOGGER.info("Package " + PACKAGE_NAME + " created");
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
