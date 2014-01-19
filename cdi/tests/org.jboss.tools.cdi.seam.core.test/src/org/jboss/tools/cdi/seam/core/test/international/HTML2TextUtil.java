/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.core.test.international;

/**
 * 
 * @author Victor Rubezhny
 */
public class HTML2TextUtil {

	/**
	 * Cuts all the html tags/comments/styles from the html-text and returns the only printable text.
	 * 
	 * @param html
	 * @return plain text
	 */
	public static String html2Text(String html) {
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

}
