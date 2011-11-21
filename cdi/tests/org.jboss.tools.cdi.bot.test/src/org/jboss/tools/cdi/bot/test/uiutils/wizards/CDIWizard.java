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

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.openon.OpenOnTest;

public class CDIWizard extends CDITestBase{
	
	private CDIWizardBaseExt wizardExt = new CDIWizardBaseExt();
	
	public void createAnnotation(String name, String packageName) {
		wizardExt.annotation(open, util, packageName, name);
	}
	
	public void createComponent(CDIWizardType component, String name,
			String packageName, String necessaryParam) {			
		createCDIComponent(component, name, packageName, necessaryParam);	
		util.waitForNonIgnoredJobs();
		/**
		 * if beans.xml is created as first component in project,
		 * it is not opened as default ==> there is no active editor
		 */
		if (component != CDIWizardType.BEANS_XML) {
			setEd(bot.activeEditor().toTextEditor());
		}		
	}
	
	public void createCDIComponent(CDIWizardType component, String name,
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
		util.waitForNonIgnoredJobs();
		if (component != CDIWizardType.BEANS_XML) {
			setEd(bot.activeEditor().toTextEditor());
		}
	}
	
	public void createClearBeansXML(String projectName) {
		if (!projectExplorer.isFilePresent(projectName, 
				"WebContent/META-INF/beans.xml".split("/")) && 
			!projectExplorer.isFilePresent(projectName, 
				"WebContent/WEB-INF/beans.xml".split("/"))) {
			createComponent(CDIWizardType.BEANS_XML, null, projectName + "/WebContent/WEB-INF", null);			
		}
		projectExplorer.openFile(projectName, "WebContent/WEB-INF/beans.xml".split("/"));
		bot.cTabItem("Source").activate();
		setEd(bot.activeEditor().toTextEditor());
		editResourceUtil.replaceClassContentByResource(OpenOnTest.class
					.getResourceAsStream("/resources/codeCompletion/beans.xml.cdi"), false);

	}

}
