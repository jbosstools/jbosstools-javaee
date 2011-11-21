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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.BeansXMLValidationErrors;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.openon.OpenOnTest;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;

/**
 * Test operates on code completion in beans.xml
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class BeansXMLValidationHelper extends CDITestBase {
	
	/**
	 * 
	 * @param validationError
	 * @return
	 */
	public boolean checkValidationErrorInBeansXML(String projectName, 
			BeansXMLValidationErrors validationError) {
		SWTBotTreeItem[] validationErrors = getBeansXMLValidationErrors(projectName);
		String error = null; 
		switch (validationError) {
		case NO_SUCH_CLASS:
			error = "There is no class with the specified name";
			break;
		case ALTERNATIVE:
			error = "must specify the name of an alternative bean class";
			break;			
		case DECORATOR:
			error = "must specify the name of a decorator bean class";
			break;
		case INTERCEPTOR:
			error = "must specify the name of an interceptor class";
			break;		
		}
		return ((validationErrors.length == 1) && 
				(validationErrors[0].getText().contains(error)));
	}
	
	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 * @return
	 */
	public boolean checkInterceptorInBeansXML(String projectName, 
			String packageName, String className) {
		createBeansXMLWithInterceptor(projectName, packageName, className);
		return getBeansXMLValidationErrors(projectName).length == 0;
	}
	
	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 * @return
	 */
	public boolean checkDecoratorInBeansXML(String projectName, 
			String packageName, String className) {
		createBeansXMLWithDecorator(projectName, packageName, className);
		return getBeansXMLValidationErrors(projectName).length == 0;
	}
	
	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 * @return
	 */
	public boolean checkAlternativeInBeansXML(String projectName, 
			String packageName, String className) {
		createBeansXMLWithAlternative(projectName, packageName, className);
		return getBeansXMLValidationErrors(projectName).length == 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public SWTBotTreeItem[] getBeansXMLValidationErrors(String projectName) {
		return ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
				+ projectName + "/WebContent/WEB-INF", "beans.xml", "CDI Problem");
	}
	
	/**
	 * Methods creates beans.xml with no tags. Location is provided by parameter.
	 * If beans.xml already exists, its content is replaced by pure beans.xml with no
	 * tags in it.
	 * @param projectName
	 */
	public void createClearBeansXML(String projectName) {
		
		createBeansXMLWithContent(projectName, "/resources/beansXML/beans.xml.cdi");
		
	}
	
	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithInterceptor(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, "/resources/beansXML/" +
				"beansXmlWithInterceptor.xml.cdi");
		editResourceUtil.replaceInEditor("Component", packageName + "." + className);
		
	}
	
	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithDecorator(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, "/resources/beansXML/" +
				"beansXmlWithDecorator.xml.cdi");
		editResourceUtil.replaceInEditor("Component", packageName + "." + className);
	}
	
	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithAlternative(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, "/resources/beansXML/" +
				"beansXmlWithAlternative.xml.cdi");
		editResourceUtil.replaceInEditor("Component", packageName + "." + className);
	}
	
	/**
	 * 
	 * @param projectName
	 * @param path
	 */
	private void createBeansXMLWithContent(String projectName, String path) {
		
		if (!projectExplorer.isFilePresent(projectName, 
				"WebContent/META-INF/beans.xml".split("/")) && 
			!projectExplorer.isFilePresent(projectName, 
				"WebContent/WEB-INF/beans.xml".split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEANS_XML, null, projectName + "/WebContent/WEB-INF", null);			
		}
		if (!projectExplorer.isFilePresent(projectName, 
				"WebContent/META-INF/beans.xml".split("/"))) {
			projectExplorer.openFile(projectName, "WebContent/WEB-INF/beans.xml".split("/"));
		}else {
			projectExplorer.openFile(projectName, "WebContent/META-INF/beans.xml".split("/"));
		}
		bot.cTabItem("Source").activate();
		setEd(bot.activeEditor().toTextEditor());
		editResourceUtil.replaceClassContentByResource(OpenOnTest.class
					.getResourceAsStream(path), 
					false);
	}

}
