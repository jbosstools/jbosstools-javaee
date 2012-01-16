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
import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.annotations.BeansXMLValidationErrors;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.openon.OpenOnTest;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;

/**
 * Helper for beans.xml validation
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class BeansXMLValidationHelper extends CDIBase {
	
	private static final String CLEAR_BEANS_XML = "/resources/beansXML/beans.xml.cdi"; 
	private static final String CLEAR_BEANS_XML_WITH_TAG = "/resources/beansXML/" +
			"beansXmlWithEmptyTag.xml.cdi";
	private static final String BEANS_XML_WITH_INTERCEPTOR = "/resources/beansXML/" +
			"beansXmlWithInterceptor.xml.cdi";
	private static final String BEANS_XML_WITH_DECORATOR = "/resources/beansXML/" +
			"beansXmlWithDecorator.xml.cdi";
	private static final String BEANS_XML_WITH_STEREOTYPE = "/resources/beansXML/" +
			"beansXmlWithStereotype.xml.cdi";
	private static final String BEANS_XML_WITH_ALTERNATIVE = "/resources/beansXML/" +
			"beansXmlWithAlternative.xml.cdi";
	
	/**
	 * Method checks if there is validation problem with text 
	 * specified by parameter validationErrors for entered project	 
	 * @param validationError
	 * @return
	 */
	public boolean checkValidationErrorInBeansXML(String projectName, 
			BeansXMLValidationErrors validationError) {
		SWTBotTreeItem[] validationErrors = getBeansXMLValidationErrors(projectName);		
		return ((validationErrors.length == 1) && 
				(validationErrors[0].getText().contains(validationError.message())));
	}
	
	/**
	 * Method checks if there is no validation problem when 
	 * creating interceptor component with entered className
	 * packageName for project with name projectName and 
	 * insert tag containing this interceptor	
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
	 * Method checks if there is no validation problem when 
	 * creating decorator component with entered className
	 * packageName for project with name projectName and 
	 * insert tag containing this decorator	
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
	 * Method checks if there is no validation problem when 
	 * creating alternative bean component with entered className
	 * packageName for project with name projectName and 
	 * insert tag containing this alternative		 
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
	 * Method gets all beans.xml validation errors showed in Problems View
	 * for entered project 
	 * @return
	 */
	public SWTBotTreeItem[] getBeansXMLValidationErrors(String projectName) {
		return ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
				+ projectName, "beans.xml", "CDI Problem");
	}
	
	/**
	 * Methods creates beans.xml with no tags for entered project.		 
	 * @param projectName
	 */
	public void createClearBeansXML(String projectName) {
		
		createBeansXMLWithContent(projectName, CLEAR_BEANS_XML);
		
	}
	
	/**
	 * Methods creates beans.xml with empty tag <> for entered project.
	 * @param projectName
	 */
	public void createClearBeansXMLWithEmptyTag(String projectName) {
		
		createBeansXMLWithContent(projectName, CLEAR_BEANS_XML_WITH_TAG);
	}
	
	/**
	 * Methods creates beans.xml with interceptor tags in it for entered project.
	 * Package and interceptor component name which should be showed in tag is 
	 * determined by parameters. If className is null, then Component text will be
	 * removed
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithInterceptor(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, BEANS_XML_WITH_INTERCEPTOR);		
		if (className == null || className.length() == 0) {
			editResourceUtil.replaceInEditor("<class>Component</class>", 
					"<class></class>");			
		} else {
			editResourceUtil.replaceInEditor("Component", packageName + "." + className);
		}
		
	}
	
	/**
	 * Methods creates beans.xml with decorator tags in it for entered project.
	 * Package and decorator component name which should be showed in tag is 
	 * determined by parameters. If className is null, then Component text will be
	 * removed
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithDecorator(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, BEANS_XML_WITH_DECORATOR);
		if (className == null || className.length() == 0) {
			editResourceUtil.replaceInEditor("<class>Component</class>", 
					"<class></class>");			
		} else {
			editResourceUtil.replaceInEditor("Component", packageName + "." + className);
		}
		
	}
	
	/**
	 * Methods creates beans.xml with stereotype tags in it for entered project.
	 * Package and stereotype component name which should be showed in tag is 
	 * determined by parameters. If className is null, then Component text will be
	 * removed
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithStereotype(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, BEANS_XML_WITH_STEREOTYPE);
		if (className == null || className.length() == 0) {
			editResourceUtil.replaceInEditor("<stereotype>Component</stereotype>", 
					"<stereotype></stereotype>");			
		} else {
			editResourceUtil.replaceInEditor("Component", packageName + "." + className);
		}
		
	}
	
	/**
	 * Methods creates beans.xml with alternative tags in it for entered project.
	 * Package and alternative bean component name which should be showed in tag is 
	 * determined by parameters. If className is null, then Component text will be
	 * removed
	 * @param projectName
	 * @param packageName
	 * @param className
	 */
	public void createBeansXMLWithAlternative(String projectName, String packageName, 
			String className) {
		
		createBeansXMLWithContent(projectName, BEANS_XML_WITH_ALTERNATIVE);
		if (className == null || className.length() == 0) {
			editResourceUtil.replaceInEditor("<class>Component</class>", 
					"<class></class>");			
		} else {
			editResourceUtil.replaceInEditor("Component", packageName + "." + className);
		}
	}
	
	/**
	 * Methods create beans.xml for entered project with content of file
	 * determined by parameter path. If there is beans.xml in project, its
	 * content is simply replaced	
	 * @param projectName
	 * @param path
	 */
	private void createBeansXMLWithContent(String projectName, String path) {
		
		if (!projectExplorer.isFilePresent(projectName, 
				"WebContent/META-INF/beans.xml".split("/")) && 
			!projectExplorer.isFilePresent(projectName, 
				"WebContent/WEB-INF/beans.xml".split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEANS_XML, null, 
					projectName + "/WebContent/WEB-INF", null);			
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
		bot.sleep(Timing.time500MS());
	}

}
