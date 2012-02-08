/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.JSFEnvironment;
import org.jboss.tools.cdi.bot.test.annotations.JSFTemplate;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewJSFProjectWizard;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewXHTMLFileWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIRefactorWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.XHTMLDialogWizard;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Before;

public class JSFTestBase extends CDITestBase {
	
	private static final Logger LOGGER = Logger.getLogger(JSFTestBase.class.getName());
	private JSFEnvironment env = JSFEnvironment.JSF_20;
	private JSFTemplate template = JSFTemplate.BLANK_LIBS;
	protected static final String WEB_FOLDER = "pages";
	
	public JSFEnvironment getEnv() {
		return env; 
	}
	
	public JSFTemplate getTemplate() {
		return template;
	}
	
	@Before
	public void checkAndCreateProject() {
		
		if (!projectHelper.projectExists(getProjectName())) {
			createJSFProjectWithCDISupport(getProjectName(), getEnv(), getTemplate());
		}
		
	}
	
	/**
	 * Method created new XHTML page with selected name
	 * @param pageName
	 */
	protected void createXHTMLPage(String pageName) {
		XHTMLDialogWizard xhtmlWizard = new NewXHTMLFileWizard().run();
		xhtmlWizard.setDestination(getProjectName() + "/" + CDIConstants.WEBCONTENT + 
				"/" + WEB_FOLDER).setName(pageName).finishWithWait();
		setEd(bot.activeEditor().toTextEditor());
	}
	
	/**
	 * Method created new XHTML page with content of resource
	 * @param pageName
	 */
	protected void createXHTMLPageWithContent(String pageName, String resource) {
		createXHTMLPage(pageName);
		editResourceUtil.replaceClassContentByResource(JSFTestBase.class.
				getResourceAsStream(resource), false);
	}
	
	
	/**
	 * Method opens context menu for CDI Refactor for selected class
	 * @param className
	 * @throws AnnotationException if no menu for CDI Refactor was found
	 */
	protected void openContextMenuForCDIRefactor(String className) throws AnnotationException {
		String text = getNamedAnnotationForClass(className);
		if (text == null) {
			throw new AnnotationException("There is no Named " +
					"annotation in class:" + className);
		}
		String renameContextMenuText = "Rename '" + 
					parseNamedAnnotation(className, text) + 
					"' Named Bean ";
		openContextMenuForTextInEditor(text, 
				IDELabel.Menu.CDI_REFACTOR, renameContextMenuText);
		bot.waitForShell("Refactoring");	
	}
	
	/**
	 * Method returns @Named annotation or null if there is no such annotation
	 * @param className
	 * @return
	 */
	private String getNamedAnnotationForClass(String className) {
		try {
			bot.editorByTitle(className + ".java");
		} catch (WidgetNotFoundException exc) {
			projectExplorer.openFile(getProjectName(), CDIConstants.JAVA_RESOURCES, CDIConstants.JAVA_SOURCE, 
									 getPackageName(), className);
		}
		
		setEd(bot.activeEditor().toTextEditor());
		for (String line : getEd().getLines()) {
			if (line.contains("@Named") &&
					!line.contains("//") && !line.contains("*")) {
				return line;
			}
		}
		return null;
	}

	/**
	 * Method parses @Named annotation and returns correct EL name for class
	 * @param className
	 * @param text
	 * @return
	 */
	private String parseNamedAnnotation(String className, String text) {
		if (!text.contains("\"")) {
			return className.substring(0,1).toLowerCase() + className.substring(1);
		} else {
			return text.split("\"")[1];
		}
		
	}

	/**
	 * Method opens context menu for text in eclipse editor
	 * @param text
	 * @param menu
	 */
	protected void openContextMenuForTextInEditor(final String text, 
			final String... menu) {
		assert menu.length > 0;		
		SWTJBTExt.selectTextInSourcePane(bot, getEd().getTitle(), 
				text, 0, text.length());	
					
		ContextMenuHelper.clickContextMenu(getEd(), menu);
		
	}
	
	/**
	 * Method changes @Named annotation to "newNamed" for selected class
	 * @param className
	 * @param newNamed
	 * @return all affected files by CDI refactoring
	 */
	protected List<String> changeNamedAnnotation(String className, String newNamed) {
		List<String> affectedFiles = new ArrayList<String>();
		try {
			openContextMenuForCDIRefactor(className);
			
			CDIRefactorWizard cdiRefactorWizard = new CDIRefactorWizard();
			cdiRefactorWizard = cdiRefactorWizard.setName(newNamed);
			cdiRefactorWizard = cdiRefactorWizard.next();
			affectedFiles = cdiRefactorWizard.getAffectedFiles();
			cdiRefactorWizard.finish();
		} catch (AnnotationException exc) {
			LOGGER.info("There is no named annotation in tested class");
			fail(exc.getMessage());
		} catch (WidgetNotFoundException exc) {
			bot.activeShell().bot().button("Close").click();
		}
		return affectedFiles;
	}
	
	/**
	 * Methods creates new JSF Project with selected name, environment and template. Finnaly
	 * it adds CDI support to this project.
	 * @param projectName
	 * @param env
	 * @param template
	 */
	private void createJSFProjectWithCDISupport(String projectName, JSFEnvironment env, 
			JSFTemplate template) {
		
		createJSFProject(projectName, env, template);
		projectHelper.addCDISupport(projectName);
		
	}

	/**
	 * Methods creates new JSF Project with selected name, environment and template.
	 * @param projectName
	 * @param env
	 * @param template
	 */
	private void createJSFProject(String projectName, JSFEnvironment env, 
			JSFTemplate template) {				
		new NewJSFProjectWizard().run().
			setName(getProjectName()).
			setEnvironment(env).
			setJSFTemplate(template).		
			finish();		
		/*
		 * workaround for non Web Perspective, click No button
		 * to not change perspective to Web Perspectives
		 * 
		 */
		try {
			bot.button("No").click();
		} catch (WidgetNotFoundException exc) {
			log.info("There is no dialog to change perspective.");
		}
		util.waitForNonIgnoredJobs();						
	}
				
}