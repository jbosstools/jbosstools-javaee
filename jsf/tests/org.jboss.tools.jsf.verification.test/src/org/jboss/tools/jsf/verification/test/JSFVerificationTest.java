/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.verification.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.verification.test.VerificationUtil;
import org.jboss.tools.common.verification.vrules.VResult;
import org.jboss.tools.test.util.TestDescription;
import org.jboss.tools.test.util.TestProjectProvider;

public class JSFVerificationTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = false;
	
	public JSFVerificationTest() {}
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.verification.test", null, "TestJSFVerification", makeCopy); 
		project = provider.getProject();
	}
	
	public void testPaths() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			fail("Wait for job interrupted");
		}
		String testName = "JSFVerificationTest:testPaths";
		ArrayList<TestDescription> tests = provider.getTestDescriptions(testName);
		System.out.println(testName + " " + (tests == null ? -1 : tests.size()));
		if(tests == null) return;
		StringBuilder sb = new StringBuilder();
		int errorCount = 0;
		StringBuilder sb2 = new StringBuilder();
		int errorCount2 = 0;
		for (int i = 0; i < tests.size(); i++) {
			TestDescription t = tests.get(i);
			String path = t.getProperty("path");
			XModelObject o = n.getModel().getByPath(path);
			if(o == null) {
				sb.append(path).append("\n");
				errorCount++;
			} else {
				String attribute = t.getProperty("attribute");
				boolean expectInvalid = "false".equals(t.getProperty("valid"));
				VResult[] result = VerificationUtil.doTestVerification(o);
				System.out.println(result);
				if((result != null && result.length > 0) != expectInvalid) {
					sb2.append(path).append('@').append(attribute).append("\n");
					errorCount2++;
					continue;
				}
				if(attribute == null) continue;
				String ov = o.getAttributeValue(attribute);
				String nv = t.getProperty("incorrectValue");
				if(nv == null) continue;
				o.setAttributeValue(attribute, nv);
				result = VerificationUtil.doTestVerification(o);
				System.out.println(result);
				if((result != null && result.length > 0) != true) {
					sb2.append(path).append('@').append(attribute).append("\n");
					errorCount2++;
					continue;
				}
				o.setAttributeValue(attribute, ov);
				
			}
		}
		assertTrue("Cannot find objects at " + errorCount + " paths\n" + sb.toString(), errorCount == 0);
		assertTrue("These " + errorCount2 + " objects are verified incorrectly\n " + sb2.toString(), errorCount2 == 0);
	}

/*
	void printPaths(XModelObject o) {
		System.out.println(o.getPath());
		XModelObject[] cs = o.getChildren();
		for (int i = 0; i < cs.length; i++) printPaths(cs[i]);
	}
*/

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

}
