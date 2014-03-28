/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.kb.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.jboss.tools.jst.web.kb.taglib.TagLibraryManager;

/**
 * @author Alexey Kazakov
 */
public class XMLCatalogTest extends TestCase {

	public void testJSFLibs() throws MalformedURLException, IOException {
		assertLib("http://java.sun.com/jsf/html");
		assertLib("http://java.sun.com/jsf/core");
	}

	public void testRichFacesLibs() throws MalformedURLException, IOException {
		assertLib("http://richfaces.org/rich");
		assertLib("http://richfaces.org/a4j");
	}

	public void testCompositeComponentsLibs() throws MalformedURLException, IOException {
		assertLib("http://java.sun.com/jsf/composite");
		assertLib("http://xmlns.jcp.org/jsf/composite");
	}

	public void assertLib(String uri) throws MalformedURLException, IOException {
		ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String file = catalog.resolveURI(uri);
		assertNotNull(file);
		File f = TagLibraryManager.getStaticTLD(uri);
		assertTrue(f.isFile());
	}
}