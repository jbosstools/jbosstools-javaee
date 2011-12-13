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

package org.jboss.tools.cdi.bot.test.seam3;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.Test;

/**
 * Test operates on generic points openOn in Seam3 using CDI tools
 * 
 * @author jjankovi
 *
 */

public class Seam3GenericOpenOnTest extends Seam3TestBase {

	private final String GENERIC_POINT_1 = "MyExtendedConfiguration ";	
	private final String GENERIC_POINT_2 = "MyConfigurationProducer.getOneConfig()";	
	private final String GENERIC_POINT_3 = "MyConfigurationProducer.getSecondConfig()";
	
	@Override
	public String getProjectName() {
		return "Seam3GenericOpenOn";
	}
	
	/**
	 * https://issues.jboss.org/browse/JBIDE-8692
	 */		
	@Test
	public void testGenericOpenOn() {

		prepareGenericOpenOn();
		
		checkFirstOpenOnAndGeneric();				
		checkSecondOpenOnAndGeneric();
		checkThirdOpenOnAndGeneric();

		String parameter = "MyConfiguration config";
		String classTitle = "MyGenericBean.java";
		checkAllGenericPointsForAtribute(parameter, classTitle);

		
		classTitle = "MyGenericBean2.java";		
		String[] atributes = {"MyConfiguration config", "MyBean c", "MyBean2 c2", 
				"MyBean3 c3", "MyBean parameter1"};
		for (String atribute : atributes) {
			checkAllGenericPointsForAtribute(atribute, classTitle);
		}
			
	}
	
	/**
	 * wizard.create all necessary components for this test
	 */
	private void prepareGenericOpenOn() {
		/**
		 * injectable beans + qualifiers + generic configuration components
		 */
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean", 
				getPackageName(), null, "/resources/seam3/generic/MyBean.java.cdi");
		editResourceUtil.replaceInEditor("MyBeanX", "MyBean");
				
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean2", 
				getPackageName(), null, "/resources/seam3/generic/MyBean.java.cdi");		
		editResourceUtil.replaceInEditor("MyBeanX", "MyBean2");
				
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean3", 
				getPackageName(), null, "/resources/seam3/generic/MyBean.java.cdi");		
		editResourceUtil.replaceInEditor("MyBeanX", "MyBean3");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyConfiguration", 
				getPackageName(), null, "/resources/seam3/generic/MyBean.java.cdi");
		editResourceUtil.replaceInEditor("MyBeanX", "MyConfiguration");					
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyGenericType", 
				getPackageName(), null, "/resources/seam3/generic/MyGenericType.java.cdi");
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Qualifier1", getPackageName(), null);
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Qualifier2", getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyExtendedConfiguration", 
				getPackageName(), null, "/resources/seam3/generic/MyExtendConfig.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyConfigurationProducer", 
				getPackageName(), null, "/resources/seam3/generic/MyConfigProd.java.cdi");
		
		/**
		 * beans which include atributes suggesting opening all the available 
		 * generic configurations 
		 */
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBeanInjections", 
				getPackageName(), null, "/resources/seam3/generic/MyBeanInjections.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyGenericBean", 
				getPackageName(), null, "/resources/seam3/generic/MyGenericBean.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyGenericBean2", 
				getPackageName(), null, "/resources/seam3/generic/MyGenericBean2.java.cdi");	
	}
		
	private void checkFirstOpenOnAndGeneric() {
		checkOpenOnAndGeneric("first1", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getOneConfig");
		checkOpenOnAndGeneric("first1", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "createMyFirstBean");	
		
		checkOpenOnAndGeneric("first2", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getSecondConfig");
		checkOpenOnAndGeneric("first2", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "createMyFirstBean");
		
		checkOpenOnAndGeneric("first3", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyExtendedConfiguration.java", "MyExtendedConfiguration");
		checkOpenOnAndGeneric("first3", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "createMyFirstBean");
	}
	
	private void checkSecondOpenOnAndGeneric() {
		checkOpenOnAndGeneric("second1", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getOneConfig");
		checkOpenOnAndGeneric("second1", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean2.java", "createMySecondBean");	
		
		checkOpenOnAndGeneric("second2", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getSecondConfig");
		checkOpenOnAndGeneric("second2", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean2.java", "createMySecondBean");
		
		checkOpenOnAndGeneric("second3", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyExtendedConfiguration.java", "MyExtendedConfiguration");
		checkOpenOnAndGeneric("second3", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean2.java", "createMySecondBean");
	}
	
	private void checkThirdOpenOnAndGeneric() {
		checkOpenOnAndGeneric("third1", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getOneConfig");
		checkOpenOnAndGeneric("third1", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "myThirdBean");	
		
		checkOpenOnAndGeneric("third2", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getSecondConfig");
		checkOpenOnAndGeneric("third2", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "myThirdBean");
		
		checkOpenOnAndGeneric("third3", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyExtendedConfiguration.java", "MyExtendedConfiguration");
		checkOpenOnAndGeneric("third3", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "myThirdBean");
	}
	
	private void checkOpenOnAndGeneric(String openOnString, String titleName, String chosenOption, 
			String afterOpenOnTitleName, String injectSelectionAtribute) {		
		openOnUtil.openOnByOption(openOnString, titleName, chosenOption);
		String activeEditor = bot.activeEditor().getTitle();
		String selectedString = bot.activeEditor().toTextEditor().getSelection();
		assertTrue(activeEditor, activeEditor.equals(afterOpenOnTitleName));
		assertTrue(selectedString, selectedString.equals(injectSelectionAtribute));
	}
	
	private void checkAllGenericPointsForAtribute(String atribute,
			String classTitle) {
		openOnUtil.openOnByOption(atribute, classTitle, "Show All Generic Configuration Points...");	
		bot.sleep(Timing.time1S());
		SWTBotTable genericPointTable = bot.table(0);
		assertTrue(checkAllGenericConfPoints(genericPointTable));
		bot.sleep(Timing.time2S());
	}
		
	private boolean checkAllGenericConfPoints(SWTBotTable genericPointTable) {
		boolean isGenericPoint1Present = false;
		boolean isGenericPoint2Present = false;
		boolean isGenericPoint3Present = false;
		for (int rowIterator = 0; rowIterator < genericPointTable.rowCount(); rowIterator++) {
			String itemInTable = genericPointTable.getTableItem(rowIterator).getText(); 
			if (itemInTable.contains(GENERIC_POINT_1)) {
				isGenericPoint1Present = true;						
				continue;
			} 
			if (itemInTable.contains(GENERIC_POINT_2)) {
				isGenericPoint2Present = true;				
				continue;
			}
			if (itemInTable.contains(GENERIC_POINT_3)) {
				isGenericPoint3Present = true;					
				continue;
			}
		}
		return isGenericPoint1Present && isGenericPoint2Present && isGenericPoint3Present;
	}

	
}
