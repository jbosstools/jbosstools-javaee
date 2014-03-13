/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core.test;

import java.io.File;

import junit.framework.TestCase;

import org.jboss.tools.jst.web.kb.taglib.TagLibraryManager;

/**
 * @author Alexey Kazakov
 */
public class XMLCatalogTest extends TestCase {

	public void testSeam22Lib() {
		File file = TagLibraryManager.getStaticTLD("http://jboss.com/products/seam/taglib");
		assertNotNull(file);
		assertTrue(file.exists());
	}

	public void testSeam23Lib() {
		File file = TagLibraryManager.getStaticTLD("http://jboss.org/schema/seam/taglib");
		assertNotNull(file);
		assertTrue(file.exists());
	}
}