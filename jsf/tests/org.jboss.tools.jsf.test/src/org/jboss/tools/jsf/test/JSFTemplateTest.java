/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jst.web.project.version.ProjectVersion;
import org.jboss.tools.jst.web.project.version.ProjectVersions;

import junit.framework.TestCase;

public class JSFTemplateTest extends TestCase {
	static String JSF_2_0 = "JSF 2.0";
	static String JSF_2_2 = "JSF 2.2";

	public void testJSFTemplate() throws Exception {
		JSFTemplate template = JSFTemplate.getInstance();
		ProjectVersions vs = template.getProjectVersions();
		String[] versionList = vs.getVersionList();

		Set<String> versionSet = toSet(versionList);
		assertTrue(versionSet.contains("JSF 1.1.02 - Reference Implementation"));
		assertTrue(versionSet.contains("JSF 1.2"));
		assertTrue(versionSet.contains("JSF 1.2 with Facelets"));
		assertTrue(versionSet.contains(JSF_2_0));
		assertTrue(versionSet.contains(JSF_2_2));

		ProjectVersion v = vs.getVersion(JSF_2_0);
		assertNotNull(v);
		String minVersion = v.getMinimalServletVersion();
		assertEquals("2.5", minVersion);
		String preferredVersion = v.getPreferredServletVersion();
		assertEquals("3.0", preferredVersion);

		String[] templateList = template.getTemplateList(JSF_2_0);
		assertEquals(3, templateList.length);
		Set<String> projects = new HashSet<String>();
		for (String p: templateList) projects.add(p);
		assertTrue(projects.contains("JSFKickStartWithoutLibs"));
		
		String s = v.getProjectTemplatesLocation();
		assertEquals("jsf-2.0", new File(s).getName());

		v = vs.getVersion(JSF_2_2);
		assertNotNull(v);
		
	}

	private Set<String> toSet(String[] list) {
		Set<String> set = new HashSet<String>();
		for (String s: list) set.add(s);
		return set;
	}

}
