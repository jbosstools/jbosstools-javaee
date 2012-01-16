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

package org.jboss.tools.cdi.bot.test.uiutils;

import java.util.List;

import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBaseExt;

public class CDIWizardHelper extends CDIBase{
	
	private CDIWizardBaseExt wizardExt = new CDIWizardBaseExt();
	
	/**
	 * Method creates Java Annotation with selected name and package	 
	 * @param name
	 * @param packageName
	 */
	public void createAnnotation(String name, String packageName) {
		wizardExt.annotation(open, util, packageName, name);
	}
	
	/**
	 * Method creates CDI component with workaround for beans.xml - 
	 * if component is beans.xml, editor is not set (there is no active editor by default)	 
	 * @param component
	 * @param name
	 * @param packageName
	 * @param necessaryParam
	 */
	public void createCDIComponent(CDIWizardType component, String name,
			String packageName, String necessaryParam) {			
		createComponent(component, name, packageName, necessaryParam);	
		util.waitForNonIgnoredJobs();
		/**
		 * if beans.xml is created as first component in project,
		 * it is not opened as default ==> there is no active editor
		 */
		if (component != CDIWizardType.BEANS_XML) {
			setEd(bot.activeEditor().toTextEditor());
		}		
	}
	
	/**
	 * Method creates CDI component with content of resource
	 * @param component
	 * @param name
	 * @param packageName
	 * @param necessaryParam
	 * @param resource
	 */
	public void createCDIComponentWithContent(CDIWizardType component, String name,
			String packageName, String necessaryParam, String resource) {			
		createCDIComponent(component, name, packageName, necessaryParam);
		if (!bot.activeEditor().getTitle().equals(name + ".java")) {
			bot.editorByTitle(name + ".java").show();
			setEd(bot.activeEditor().toTextEditor());
		}
		editResourceUtil.replaceClassContentByResource(CDIWizardHelper.class.
				getResourceAsStream(resource), false);
	}
	
	/**
	 * Method creates larger number("amount") of the same component. 	 
	 * @param component
	 * @param amount
	 * @param baseName
	 * @param packageBaseName
	 * @param necessaryParam
	 * @param differentPackages
	 */
	public void createCDIComponents(CDIWizardType component, int amount, String baseName, 
			String packageBaseName, String necessaryParam, boolean differentPackages) {
		
		for (int i = 1; i <= amount; i++) {
			String packageName = (differentPackages) ? packageBaseName + i : packageBaseName;
			createCDIComponent(component, baseName + i, packageName, necessaryParam);
		}
		
	}
	
	/**
	 * Method creates larger number("amount") of the same component with using 
	 * List of class names
	 * @param component
	 * @param packageBaseName
	 * @param classNames
	 * @param necessaryParam
	 * @param differentPackages
	 */
	public void createCDIComponents(CDIWizardType component, String packageBaseName, 
			List<String> classNames, String necessaryParam, boolean differentPackages) {
		if (classNames == null) {
			throw new IllegalArgumentException("List with class names should not be " +
					"null");
		}
		if (classNames.size() == 0) {
			throw new IllegalArgumentException("List with class names should not be " +
					"empty");
		}
		for (int i = 0; i < classNames.size(); i++) {
			String packageName = (differentPackages) ? packageBaseName + i : packageBaseName;
			createCDIComponent(component, classNames.get(i), packageName, necessaryParam);
		}
		
	}
	
	/**
	 * Method that actually creates CDI component according to parameter
	 * @param component
	 * @param name
	 * @param packageName
	 * @param necessaryParam
	 */
	private void createComponent(CDIWizardType component, String name,
			String packageName, String necessaryParam) {
		switch (component) {
		case STEREOTYPE:
			boolean alternative = false;
			boolean regInBeansXml = false;
			if (necessaryParam != null) {
				if (necessaryParam.equals("alternative+beansxml")) {
					alternative = true;
					regInBeansXml = true;
				} else if (necessaryParam.equals("alternative")) {
					alternative = true;
				}
			}
			wizardExt.stereotype(packageName, name, null, null, false, false, alternative, regInBeansXml,
					false).finish();
			break;
		case QUALIFIER:
			wizardExt.qualifier(packageName, name, false, false).finish();
			break;
		case SCOPE:
			wizardExt.scope(packageName, name, false, false, true, false).finish();
			break;
		case BEAN:
			alternative = false;
			regInBeansXml = false;
			if (necessaryParam != null) {
				if (necessaryParam.equals("alternative+beansxml")) {
					alternative = true;
					regInBeansXml = true;
				} else if (necessaryParam.equals("alternative")) {
					alternative = true;
				}
			}
			wizardExt.bean(packageName, name, true, false, false, false, alternative, regInBeansXml, null, null,
					null, null).finish();
			break;
		case INTERCEPTOR:
			wizardExt.interceptor(packageName, name, null, null, null, false).finish();
			break;
		case DECORATOR:
			wizardExt.decorator(packageName, name, necessaryParam, null, true, false, false, false)
					.finish();
			break;
		case ANNOTATION_LITERAL:
			wizardExt.annLiteral(packageName, name, true, false, false, false, null).finish();
			break;
		case INTERCEPTOR_BINDING:
			wizardExt.binding(packageName, name, null, true, false).finish();
			break;
		case BEANS_XML:
			wizardExt.beansXML(packageName).finish();			
			break;
		}					
	}
	
}
