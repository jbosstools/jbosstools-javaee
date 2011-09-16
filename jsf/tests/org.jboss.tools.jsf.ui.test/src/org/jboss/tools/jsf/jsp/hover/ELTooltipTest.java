/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsp.hover;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test cases for JBIDE-9448 issue 
 * 
 * @author Victor Rubezhny
 */
public class ELTooltipTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";

	private static final String EL_PREFIX[] = {"value=\"#{user", "value=\"#{user.name", "action=\"#{user.sayHello"};
	private static final String EL_VALUE[] = {"user", "name", "sayHello"};
	private static final String EL_TOOLTIP_TEXT[] = {
		"<html><body text=\"#000000\" bgcolor=\"#ffffe1\"><h5><img style='position: relative; width: 16px; height: 16px; top: 2px; left: 2px; ' src='file:/home/jeremy/projects/junit-workspace/.metadata/.plugins/org.eclipse.jdt.ui/jdt-images/0.png'>\n" + 
				"<span style='word-wrap:break-word;margin-left: 2px; margin-top: 2px; '>demo.User</span></span></h5><p>Created by JBoss Tools</body></html>",
		"<html><body text=\"#000000\" bgcolor=\"#ffffe1\">- <span style='word-wrap:break-word;'>void demo.<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User'>User</a>.setName(<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User~setName~QString;%E2%98%82String'>String</a> name)</span><br/>- <span style='word-wrap:break-word;'><a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User~getName%E2%98%82String'>String</a> demo.<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User'>User</a>.getName()</span><br/><br/><h5><img style='position: relative; width: 16px; height: 16px; top: 2px; left: 2px; ' src='file:/home/jeremy/projects/junit-workspace/.metadata/.plugins/org.eclipse.jdt.ui/jdt-images/1.png'>\n" +
				"<span style='word-wrap:break-word;margin-left: 2px; margin-top: 2px; '>void demo.<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User'>User</a>.setName(<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User~setName~QString;%E2%98%82String'>String</a> name)</span></span></h5><br/><h5><img style='position: relative; width: 16px; height: 16px; top: 2px; left: 2px; ' src='file:/home/jeremy/projects/junit-workspace/.metadata/.plugins/org.eclipse.jdt.ui/jdt-images/1.png'>\n" +
				"<span style='word-wrap:break-word;margin-left: 2px; margin-top: 2px; '><a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User~getName%E2%98%82String'>String</a> demo.<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User'>User</a>.getName()</span></span></h5></body></html>",
		"<html><body text=\"#000000\" bgcolor=\"#ffffe1\"><h5><img style='position: relative; width: 16px; height: 16px; top: 2px; left: 2px; ' src='file:/home/jeremy/projects/junit-workspace/.metadata/.plugins/org.eclipse.jdt.ui/jdt-images/1.png'>\n" +
				"<span style='word-wrap:break-word;margin-left: 2px; margin-top: 2px; '><a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User~sayHello%E2%98%82String'>String</a> demo.<a class='header' href='eclipse-javadoc:%E2%98%82=JSF2KickStartWithoutLibs/JavaSource%3Cdemo%7BUser.java%E2%98%83User'>User</a>.sayHello()</span></span></h5></body></html>"
	};
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(ELTooltipTest.class);
	}

	@SuppressWarnings("deprecation")
	private void doELTooltipTest(String prefix, String value, String compare) {
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
		assertFalse("Required node '" + prefix + "' not found in document", (start == -1));
		int offsetToTest = start + prefix.length();

		ITextHover hover = getTextHover(viewer, offsetToTest - 1);
		assertNotNull("Hover not found for value: '" + value + "'", hover);
		
		String hoverText = null;
		if (hover instanceof ITextHoverExtension2) {
			Object hoverInfo2 = ((ITextHoverExtension2)hover).getHoverInfo2(viewer, hover.getHoverRegion(viewer, offsetToTest));
			hoverText = String.valueOf(hoverInfo2);
		} else {
			hoverText = ((ITextHover)hover).getHoverInfo(viewer, hover.getHoverRegion(viewer, offsetToTest));
		}			
		String hoverTextValue = html2Text(hoverText);
		String compareValue = html2Text(compare);
//		System.out.println("Hover Text: [" + hoverTextValue + "]\nExpected Value: [" + compareValue + "]\nEqual: " + compareValue.equalsIgnoreCase(hoverTextValue) + "\n");
		assertTrue("Hover exists but its value is not expected:\nHover Text: [" + hoverTextValue + "]\nExpected Value: [" + compareValue + "]", compareValue.equalsIgnoreCase(hoverTextValue));
	}
	
	String html2Text(String html) {
		StringBuilder sb = new StringBuilder();
		int state = 0;
		for (char ch : html.toCharArray()) {
			switch (state) {
			case (int)'<':
				// Read to null until '>'-char is read
				if (ch != '>')
					continue;
				state = 0;
				break;
			default:
				if (ch == '<') {
					state = '<';
					continue;
				}
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}
	
	public ITextHover getTextHover(ITextViewer viewer, int offset) {
		try {
			Method m = TextViewer.class.getDeclaredMethod("getTextHover", int.class, int.class); //$NON-NLS-1$
			m.setAccessible(true);
			return (ITextHover)m.invoke(viewer, Integer.valueOf(offset), Integer.valueOf(0));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return null;
	}

	public void testELTooltip() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			for (int i = 0; i < EL_PREFIX.length; i++) {
				doELTooltipTest(EL_PREFIX[i], EL_VALUE[i], EL_TOOLTIP_TEXT[i]);
			}
		} finally {
			closeEditor();
		}
	}
}
