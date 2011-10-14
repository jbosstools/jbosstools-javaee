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
package org.jboss.tools.cdi.seam.core.test.international;

import java.lang.reflect.Method;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;

/**
 * The JUnit test cases for JBIDE-9731 issues 
 * 
 * @author Victor Rubezhny
 */
public class SeamResourceBundleELTooltipTest extends SeamCoreTest {
	private static final String PAGE_NAME = "WebContent/seam-international.xhtml";

	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();

	private static final String EL_PREFIX[] = {
		"value=\"#{bundles.messages",
		"value=\"#{bundles.messages.home_header"
	};
	private static final String EL_VALUE[] = {"bundles.messages", "prompt"};
	private static final String EL_TOOLTIP_TEXT[] = {
		"<b>Base Name:</b> messages<br><br><b>Resource Bundle:</b> /SeamCoreTest/src/messages.properties<br><b>Resource Bundle:</b> /SeamCoreTest/src/messages_de.properties<br>",
		"<b>Property:</b> home_header<br><b>Base Name:</b> messages<br><br><b>Resource Bundle:</b> /SeamCoreTest/src/messages.properties<br><b>Value:</b> About this example application<br><br>"
	};

	@SuppressWarnings("deprecation")
	private void doELTooltipTest(String prefix, String value, String compare) {
		String documentContent = caTest.getDocument().get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
		assertFalse("Required node '" + prefix + "' not found in document", (start == -1));
		int offsetToTest = start + prefix.length();
		
		ITextHover hover = getTextHover(caTest.getViewer(), offsetToTest - 1);
		assertNotNull("Hover not found for value: '" + value + "'", hover);
		
		String hoverText = null;
		if (hover instanceof ITextHoverExtension2) {
			Object hoverInfo2 = ((ITextHoverExtension2)hover).getHoverInfo2(caTest.getViewer(), hover.getHoverRegion(caTest.getViewer(), offsetToTest));
			hoverText = String.valueOf(hoverInfo2);
		} else {
			hoverText = ((ITextHover)hover).getHoverInfo(caTest.getViewer(), hover.getHoverRegion(caTest.getViewer(), offsetToTest));
		}			
		String hoverTextValue = html2Text(hoverText);
		String compareValue = html2Text(compare);
		//System.out.println("Hover Text: [" + hoverTextValue + "]\nExpected Value: [" + compareValue + "]\nEqual: " + compareValue.equalsIgnoreCase(hoverTextValue) + "\n");
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
	
	public void testELTooltip() throws Exception {
		caTest.setProject(getTestProject());
		caTest.openEditor(PAGE_NAME);
		try {
			for (int i = 0; i < EL_PREFIX.length; i++) {
				doELTooltipTest(EL_PREFIX[i], EL_VALUE[i], EL_TOOLTIP_TEXT[i]);
			}
		} finally {
			caTest.closeEditor();
		}
	}
}
