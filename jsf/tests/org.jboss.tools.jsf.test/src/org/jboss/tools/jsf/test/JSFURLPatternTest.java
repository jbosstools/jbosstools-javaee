/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.test;

import org.jboss.tools.jsf.web.pattern.PrefixUrlPattern;

import junit.framework.TestCase;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSFURLPatternTest extends TestCase {

	public JSFURLPatternTest() {}

	public void testPrefixURLPattern() {
		PrefixUrlPattern p = new PrefixUrlPattern();
		
		assertTrue(p.isJSFUrl("/faces/a.xhtml"));
		assertTrue(p.isJSFUrl("faces/a.xhtml"));

		assertFalse(p.isJSFUrl("faces1/a.xhtml"));

		assertEquals("/a.xhtml", p.getJSFPath("/faces/a.xhtml"));
		assertEquals("/a.xhtml", p.getJSFPath("faces/a.xhtml"));

		p.setPrefix("/jsf/");
		
		assertTrue(p.isJSFUrl("/jsf/a.xhtml"));
		assertTrue(p.isJSFUrl("jsf/a.xhtml"));

		assertEquals("/a.xhtml", p.getJSFPath("/jsf/a.xhtml"));
		assertEquals("/a.xhtml", p.getJSFPath("jsf/a.xhtml"));
	}
}
