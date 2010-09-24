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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;

/**
 * @author Alexey Kazakov
 */
public class CATest extends TestCase {

	private IProject project;
	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/test.jsp";
	private String[] beanProposals = new String[] {"example", "example.com", "fish", "game", "haddock", "salmon", "sheep", "tunaFarm", "whitefish", "wolf"};
	private String[] propertyProposals = new String[] {"game.value", "game.initialize()"};

	public CATest() {
		super();
		try {
			project = TCKTest.importPreparedProject("/tests/lookup");
			caTest.setProject(project);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEL() {
		caTest.checkProposals(PAGE_NAME, "value=\"#{", 9, beanProposals, false);
		caTest.checkProposals(PAGE_NAME, "rendered=\"#{(game.", 18, propertyProposals, false);
	}
}