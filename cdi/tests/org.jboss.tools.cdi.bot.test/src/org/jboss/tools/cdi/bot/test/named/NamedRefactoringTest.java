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

package org.jboss.tools.cdi.bot.test.named;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.jsf.JSFTestBase;
import org.jboss.tools.cdi.bot.test.uiutils.CollectionsUtil;
import org.junit.After;
import org.junit.Test;

/**
 * Test operates on @Named refactoring  
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class NamedRefactoringTest extends JSFTestBase {

	private static final String MANAGED_BEAN_1 = "ManagedBean1";
	private static final String MANAGED_BEAN_2 = "ManagedBean2";
	private static final String INDEX_XHTML_1= "index1.xhtml";
	private static final String INDEX_XHTML_2= "index2.xhtml";
	private static final String INDEX_XHTML_3= "index3.xhtml";
	private static final String NEW_NAMED_PARAM = "bean2";	

	@After
	public void waitForJobs() {
		editResourceUtil.deletePackage(getProjectName(), getPackageName());
		editResourceUtil.deleteWebFolder(getProjectName(), WEB_FOLDER);
	}
	
	@Test
	public void testNamedAnnotationWithParamRefactor() {
				
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, MANAGED_BEAN_1, 
				getPackageName(), null, "/resources/jsf/ManagedBeanParamNamed.java.cdi");		
		
		createXHTMLPageWithContent(INDEX_XHTML_1, "/resources/jsf/index1.xhtml.cdi");
		createXHTMLPageWithContent(INDEX_XHTML_3, "/resources/jsf/index3.xhtml.cdi");
		
		bot.editorByTitle(MANAGED_BEAN_1 + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		
		Collection<String> affectedFiles = changeNamedAnnotation(MANAGED_BEAN_1, 
				NEW_NAMED_PARAM);
		Collection<String> expectedAffectedFiles = Arrays.asList(
				MANAGED_BEAN_1 + ".java", INDEX_XHTML_1, INDEX_XHTML_3);
	
		for (String affectedFile : affectedFiles) {
			bot.editorByTitle(affectedFile).save();
		}
	
		assertEquals(expectedAffectedFiles.size(), affectedFiles.size());
		assertTrue(CollectionsUtil.compareTwoCollectionsEquality(
				expectedAffectedFiles, affectedFiles));
		
		assertTrue(bot.editorByTitle(MANAGED_BEAN_1 + ".java").toTextEditor().getText().
			contains("@Named(\"" + NEW_NAMED_PARAM + "\""));
		
		assertTrue(bot.editorByTitle(INDEX_XHTML_1).toTextEditor().getText().
				contains("#{" + NEW_NAMED_PARAM));
		
		assertTrue(bot.editorByTitle(INDEX_XHTML_3).toTextEditor().getText().
				contains("#{" + NEW_NAMED_PARAM));
		
	}
	
	@Test
	public void testNamedAnnotationWithoutParamRefactor() {
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, MANAGED_BEAN_2, 
				getPackageName(), null, "/resources/jsf/ManagedBeanNoParamNamed.java.cdi");	
		
		createXHTMLPageWithContent(INDEX_XHTML_2, "/resources/jsf/index2.xhtml.cdi");
		createXHTMLPageWithContent(INDEX_XHTML_3, "/resources/jsf/index3.xhtml.cdi");
		
		bot.editorByTitle(MANAGED_BEAN_2 + ".java").show();
		setEd(bot.activeEditor().toTextEditor());

		Collection<String> affectedFiles = changeNamedAnnotation(MANAGED_BEAN_2, NEW_NAMED_PARAM);
		Collection<String> expectedAffectedFiles = Arrays.asList(
				MANAGED_BEAN_2 + ".java", INDEX_XHTML_2, INDEX_XHTML_3);
	
		for (String affectedFile : affectedFiles) {
			bot.editorByTitle(affectedFile).save();
		}
	
		assertEquals(expectedAffectedFiles.size(), affectedFiles.size());
		assertTrue(CollectionsUtil.compareTwoCollectionsEquality(
				expectedAffectedFiles, affectedFiles));
		
		assertTrue(bot.editorByTitle(MANAGED_BEAN_2 + ".java").toTextEditor().getText().
			contains("@Named(\"" + NEW_NAMED_PARAM + "\""));
		
		assertTrue(bot.editorByTitle(INDEX_XHTML_2).toTextEditor().getText().
				contains("#{" + NEW_NAMED_PARAM));
		
		assertTrue(bot.editorByTitle(INDEX_XHTML_3).toTextEditor().getText().
				contains("#{" + NEW_NAMED_PARAM));
		
	}
	
	@Test
	public void testNamedAnnotationWithoutELRefactoring() {
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, MANAGED_BEAN_2, 
				getPackageName(), null, "/resources/jsf/ManagedBeanNoParamNamed.java.cdi");	
		
		createXHTMLPageWithContent(INDEX_XHTML_2, "/resources/jsf/index1.xhtml.cdi");		
		
		bot.editorByTitle(MANAGED_BEAN_2 + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
			
		Collection<String> affectedFiles = changeNamedAnnotation(MANAGED_BEAN_2, NEW_NAMED_PARAM);
		Collection<String> expectedAffectedFiles = Arrays.asList(MANAGED_BEAN_2 + ".java");
	
		for (String affectedFile : affectedFiles) {
			bot.editorByTitle(affectedFile).save();
		}
	
		assertEquals(expectedAffectedFiles.size(), affectedFiles.size());
		assertTrue(CollectionsUtil.compareTwoCollectionsEquality(
				expectedAffectedFiles, affectedFiles));
		
		assertTrue(bot.editorByTitle(MANAGED_BEAN_2 + ".java").toTextEditor().getText().
			contains("@Named(\"" + NEW_NAMED_PARAM + "\""));
		
		assertTrue(!bot.editorByTitle(INDEX_XHTML_2).toTextEditor().getText().
				contains("#{" + NEW_NAMED_PARAM));
				
	}

	

}
