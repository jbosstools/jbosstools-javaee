/******************************************************************************* 
 * Copyright (c) 2011-2013 Red Hat, Inc. 
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

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.jboss.tools.jst.web.ui.base.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test cases for JBIDE-9448, JBIDE-9731 issues 
 * 
 * @author Victor Rubezhny
 */
public class ELTooltipTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";

	private static final String EL_PREFIX[] = {
				"value=\"#{user", 
				"value=\"#{user.name", 
				"action=\"#{user.sayHello",
				"label=\"${msgs",
				"label=\"${msgs.prompt"
			};
	private static final String EL_VALUE[] = {"user", "name", "sayHello", "msgs", "prompt"};
	private static final String EL_TOOLTIP_TEXT[] = {
		"demo.UserCreated by JBoss Tools",
		"String demo.User.getName()No Javadoc could be found     void demo.User.setName(String name)No Javadoc could be found",
		"String demo.User.sayHello()No Javadoc could be found",
		"Base Name: resourcesResource Bundle: /JSF2KickStartWithoutLibs/JavaSource/resources.properties",
		"Property: promptBase Name: resourcesResource Bundle: /JSF2KickStartWithoutLibs/JavaSource/resources.propertiesValue: Your Name:"
				
	};
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
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
		
		// 
		// JBIDE-16135: CSS part contains the fontnames that are OS and setup dependent,
		// So we should exclude it from compare
		// 
		int styleStart = html.toLowerCase().indexOf("<style");
		int styleEnd = html.toLowerCase().indexOf("/style>");
		
		while (styleStart != -1 && styleEnd > styleStart) {
			html = html.substring(0, styleStart) + html.substring(styleEnd + "/style>".length());
			styleStart = html.toLowerCase().indexOf("<style");
			styleEnd = html.toLowerCase().indexOf("/style>");
		}
		// JBIDE-16135: pragmas and comments should be remived also
		int commentStart = html.indexOf("<!--");
		int commentEnd = html.indexOf("-->");
		while (commentStart != -1 && commentEnd > commentStart) {
			html = html.substring(0, commentStart) + html.substring(commentEnd + "-->".length());
			commentStart = html.indexOf("<!--");
			commentEnd = html.indexOf("-->");
		}
		html = html.trim();
		
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
		return sb.toString().trim();
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
