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
package org.jboss.tools.cdi.ui.test;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.osgi.framework.Bundle;

/**
 * @author Alexey Kazakov
 */
public class CATest extends TCKTest {

	private IProject project;
	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/test.jsp";
	private static final String RESOURCE_NAME = "/resources/CATest/test.jsp";
	private String[] beanProposals = new String[] {"example", "example.com", "fish", "game", "haddock", "salmon", "sheep", "tunaFarm", "whitefish", "wolf"};
	private String[] propertyProposals = new String[] {"game.value", "game.initialize"};
	private final static String UI_TEST_PLUGIN_ID = "org.jboss.tools.cdi.ui.test";

	public CATest() {
		super();
		try {
			project = importPreparedProject("/tests/lookup");
			Bundle ui = Platform.getBundle(UI_TEST_PLUGIN_ID);
			String projectPath = project.getLocation().toString();
			String resourcePath = FileLocator.resolve(ui.getEntry(RESOURCE_NAME)).getFile();

			File from = new File(resourcePath);
			File to = new File(projectPath, PAGE_NAME);

			FileUtil.copyFile(from, to);
			caTest.setProject(project);
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			JobUtils.waitForIdle(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEL() {
		JobUtils.waitForIdle(2000);
		caTest.checkProposals(PAGE_NAME, "value=\"#{", 9, beanProposals, false);
		caTest.checkProposals(PAGE_NAME, "rendered=\"#{(game.", 18, propertyProposals, false);
	}
}