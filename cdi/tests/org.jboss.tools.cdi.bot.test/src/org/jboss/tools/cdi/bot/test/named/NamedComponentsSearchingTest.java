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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBaseExt;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.SearchNamedDialogWizard;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.After;
import org.junit.Test;

/**
 * Test operates on @Named searching  
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class NamedComponentsSearchingTest extends CDITestBase {

	private final CDIWizardBaseExt wizardExt = new CDIWizardBaseExt();
	private final String beanName = "Bean1";
	private final String stereotypeName = "Stereotype1";
	private static SearchNamedDialogWizard namedDialog = null;
	
	@Override
	public String getProjectName() {
		return "CDINamedSearching";
	}
	
	@After
	public void waitForJobs() {
		editResourceUtil.deletePackage(getProjectName(), getPackageName());
		util.waitForNonIgnoredJobs();
	}
	
	@Test
	public void testSearchDefaultNamedBean() {
		
		wizardExt.bean(getPackageName(), beanName, true, false, false, 
				false, false, false, "", null, null, null).finish();
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(beanName);		
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
		
	}
	
	@Test
	public void testSearchNamedParameterBean() {
		
		String namedParam = "someBean";
		
		wizardExt.bean(getPackageName(), beanName, true, false, false, 
				false, false, false, namedParam, null, null, null).finish();
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(namedParam);		
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
	
	}
	
	@Test
	public void testSearchNamedParameterChangeBean() {
				
		String namedParam = "someBean";
		String changedNamedParam = "someOtherBean";
		
		wizardExt.bean(getPackageName(), beanName, true, false, false, 
				false, false, false, namedParam, null, null, null).finish();
		setEd(bot.activeEditor().toTextEditor());
				
		namedDialog = openSearchNamedDialog().setNamedPrefix(namedParam);		
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
		
		editResourceUtil.replaceInEditor(namedParam, changedNamedParam);
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(namedParam);		
		assertTrue(namedDialog.matchingItems().size() == 0);
		namedDialog = namedDialog.setNamedPrefix(changedNamedParam);
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
		
	}
	
	@Test
	public void testSearchTwoSameNamedBean() {
				
		String beanName2 = "Bean2";
		String namedParam = "someBean";
		
		wizardExt.bean(getPackageName(), beanName, true, false, false, 
				false, false, false, namedParam, null, null, null).finish();
		wizardExt.bean(getPackageName(), beanName2, true, false, false, 
				false, false, false, namedParam, null, null, null).finish();
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(namedParam);
		List<String> matchingItems = namedDialog.matchingItems();
		assertTrue(matchingItems.size() == 2);
		for (String matchingItem : matchingItems) {
			if (matchingItem.contains(beanName)) {
				namedDialog.setMatchingItems(matchingItem);
				break;
			}
		}		
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
		
	}
	
	@Test
	public void testSearchBeansWithSamePrefixNamedParam() {
	
		String[] beansNames = {"SomeBean", "SomeBean1", "SomeBean2", "SomeBean22", "SomeOtherBean"};
		
		Map<String, Integer> prefixesWithCount = new LinkedHashMap<String, Integer>();
		prefixesWithCount.put(beansNames[0], 4);
		prefixesWithCount.put(beansNames[1], 1);
		prefixesWithCount.put(beansNames[2], 2);
		prefixesWithCount.put(beansNames[3], 1);
		prefixesWithCount.put("Some", 5);
		
		for (String beanName : beansNames) {
			wizardExt.bean(getPackageName(), beanName, true, false, false, 
					false, false, false, "", null, null, null).finish();
		}
		
		for (String prefix : prefixesWithCount.keySet()) {
			namedDialog = openSearchNamedDialog().setNamedPrefix(prefix);		
			assertTrue(namedDialog.matchingItems().size() == prefixesWithCount.get(prefix));
			namedDialog.cancel();			
		}
		
	}
	
	@Test
	public void testSearchBeanWithStereotype() {
		
		wizardExt.stereotype(getPackageName(), stereotypeName, null, null, false, true, 
				false, false, false).finish();
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, beanName, getPackageName(), 
				null, "/resources/named/BeanWithStereotype.java.cdi");
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(beanName);		
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
		
	}
	
	@Test
	public void testSearchBeanWithStereotypeAndNamedParam() {
		
		String namedParam = "someBean";
		
		wizardExt.stereotype(getPackageName(), stereotypeName, null, null, false, true, 
				false, false, false).finish();
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, beanName, getPackageName(), 
				null, "/resources/named/BeanWithStereotypeAndNamed.java.cdi");
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(beanName);		
		assertTrue(namedDialog.matchingItems().size() == 0);
		namedDialog = namedDialog.setNamedPrefix(namedParam);
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
	}
	
	@Test
	public void testSearchBeanWithStereotypeWithNamedParamChange() {
		
		String namedParam = "someBean";
		String changedNamedParam = "someOtherBean";
		
		wizardExt.stereotype(getPackageName(), stereotypeName, null, null, false, true, 
				false, false, false).finish();
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, beanName, getPackageName(), 
				null, "/resources/named/BeanWithStereotypeAndNamed.java.cdi");
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(beanName);		
		assertTrue(namedDialog.matchingItems().size() == 0);
		namedDialog = namedDialog.setNamedPrefix(namedParam);
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
		
		editResourceUtil.replaceInEditor(namedParam, changedNamedParam);
		
		namedDialog = openSearchNamedDialog().setNamedPrefix(namedParam);		
		assertTrue(namedDialog.matchingItems().size() == 0);
		namedDialog = namedDialog.setNamedPrefix(changedNamedParam);
		assertTrue(namedDialog.matchingItems().size() == 1);
		namedDialog.ok();
		assertTrue(bot.activeEditor().getTitle().equals(beanName + ".java"));
		assertTrue(bot.activeEditor().toTextEditor().getSelection().equals(beanName));
	}
	
	private SearchNamedDialogWizard openSearchNamedDialog() {		
		bot.menu("Navigate").menu("Open CDI Named Bean").click();
		bot.sleep(Timing.time500MS());
		return new SearchNamedDialogWizard();
	}

}
