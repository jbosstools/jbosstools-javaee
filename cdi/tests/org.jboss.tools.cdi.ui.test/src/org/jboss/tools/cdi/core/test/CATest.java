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
package org.jboss.tools.cdi.core.test;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.osgi.framework.Bundle;

/**
 * @author Alexey Kazakov
 */
public class CATest extends TCKTest {

	private IProject project;
	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "/resources/CATest/test.jsp";
	private String[] beanProposals = new String[] {"sheep"};
	private String[] propertyProposals = new String[] {"sheep"};
	private final static String UI_TEST_PLUGIN_ID = "org.jboss.tools.cdi.ui.test";

	public CATest() {
		super();
		try {
			project = importPreparedProject("/lookup");
			Bundle core = Platform.getBundle(PLUGIN_ID);
			Bundle ui = Platform.getBundle(UI_TEST_PLUGIN_ID);
			String projectPath = FileLocator.resolve(core.getEntry(PROJECT_PATH)).getFile();
			String resourcePath = FileLocator.resolve(ui.getEntry(PAGE_NAME)).getFile();

			File from = new File(resourcePath);
			File to = new File(projectPath, "/WebContent");

			FileUtil.copyFile(from, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEL() {
		caTest.checkProposals(PAGE_NAME, "value=\"#{", 0, beanProposals, false);
		caTest.checkProposals(PAGE_NAME, "rendered=\"#{(game.", 0, propertyProposals, false);
	}
}