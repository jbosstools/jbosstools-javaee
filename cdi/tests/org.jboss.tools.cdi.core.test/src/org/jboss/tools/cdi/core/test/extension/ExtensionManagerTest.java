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
package org.jboss.tools.cdi.core.test.extension;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.jboss.tools.cdi.core.extension.CDIExtensionManager;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ExtensionManagerTest extends TestCase {

	public void testExtensionManager() throws Exception {
		String runtimeClassName = "org.jboss.tools.cdi.core.fake.FakeExtension";
		CDIExtensionManager m = new CDIExtensionManager();
		Set<String> set = new HashSet<String>();
		m.setRuntimes("path1", set);
		m.pathRemoved("path1");

		set.add(runtimeClassName);
		m.setRuntimes("path1", set);
		assertTrue(m.isCDIExtensionAvailable(runtimeClassName));
		m.pathRemoved("path1");
		assertFalse(m.isCDIExtensionAvailable(runtimeClassName));

		set.add("abc");
		m.setRuntimes("path1", set);
		m.pathRemoved("path1");
		
		set.clear();
		set.add("abc");
		m.setRuntimes("path1", set);
		m.pathRemoved("path1");
	}

}
