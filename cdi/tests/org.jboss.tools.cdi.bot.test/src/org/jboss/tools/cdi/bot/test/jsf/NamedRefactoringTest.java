/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.jsf;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.CollectionsUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on @Named refactoring  
 * 
 * @author Jaroslav Jankovic
 * 
 */
@Require(clearProjects = true, perspective = "Java EE", 
		server = @Server(state = ServerState.NotRunning, 
		version = "6.0", operator = ">=")) 
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class NamedRefactoringTest extends JSFTestBase {

	private static final String MANAGED_BEAN = "ManagedBean"; 
	private static final String INDEX_XHTML= "index.xhtml";
	
	@Override
	public String getProjectName() {
		return "CDIRefactoring";
	}
				
	@Test
	public void testNamedAnnotationRefactor() {
		
		wizard.createCDIComponent(CDIWizardType.BEAN, MANAGED_BEAN, getPackageName(), null);
		editResourceUtil.replaceClassContentByResource(NamedRefactoringTest.class.
				getResourceAsStream("/resources/jsf/ManagedBean.java.cdi"), false);
		
		createXHTMLPage("index.xhtml");		
		editResourceUtil.replaceClassContentByResource(NamedRefactoringTest.class.
				getResourceAsStream("/resources/jsf/index.xhtml.cdi"), false);

		bot.editorByTitle(MANAGED_BEAN + ".java").show();
		setEd(bot.activeEditor().toTextEditor());

		String newNamed = "bean2";		
		Collection<String> affectedFiles = changeNamedAnnotation(MANAGED_BEAN, newNamed);
		Collection<String> expectedAffectedFiles = Arrays.asList(
				MANAGED_BEAN + ".java", INDEX_XHTML);
	
		for (String affectedFile : affectedFiles) {
			bot.editorByTitle(affectedFile).save();
		}
	
		assertEquals(expectedAffectedFiles.size(), affectedFiles.size());
		assertTrue(CollectionsUtil.compareTwoCollectionsEquality(
				expectedAffectedFiles, affectedFiles));
		
		assertTrue(bot.editorByTitle(MANAGED_BEAN + ".java").toTextEditor().getText().
			contains("@Named(\"" + newNamed + "\""));
		
		assertTrue(bot.editorByTitle(INDEX_XHTML).toTextEditor().getText().
				contains("#{" + newNamed + ".submit()}"));
		
	}

	

}
