/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.vpe.jsf.template.ComponentUtil;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.vpe.VpePlugin;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;

/**
 * Checks f:view's locale workflow.
 * 
 * @author dmaliarevich
 */
public class TestFViewLocaleAttribute_JBIDE5218 extends VpeTest {

	private static final String DEFAULT_LOCALE_PAGE = "defaultLocale.jsp"; //$NON-NLS-1$
	private static final String LOCALE_ATTRIBUTE_WITH_DEFAULT_LOCALE_PAGE = "localeAndDefault.jsp"; //$NON-NLS-1$
	private static final String LOCALE_ATTRIBUTE_PAGE = "JBIDE/5218/localeAttribute.xhtml"; //$NON-NLS-1$
	private static final String SEVERAL_FVIEWS_PAGE = "JBIDE/5218/severalFViews.xhtml"; //$NON-NLS-1$
	private static final String CHANGE_LOCALE_AND_REFRESH_PAGE = "JBIDE/5218/changeLocaleAndRefresh.xhtml"; //$NON-NLS-1$
	private static final String NO_DEFLOC_ATTRIBUTE_PAGE = "Lattr.jsp"; //$NON-NLS-1$
	private static final String NO_DEFLOC_SEVERAL_FVIEWS_PAGE = "LSeveral.jsp"; //$NON-NLS-1$
	private static final String NO_DEFLOC_CHANGE_REFRESH_PAGE = "LChangeRefresh.jsp"; //$NON-NLS-1$
	private static final String NO_DEFLOC_ONE_LOAD_BUNDLE_PAGE = "LOneLoadBundle.jsp"; //$NON-NLS-1$
	
	private static final String HELLO_DE = "Guten Tag!"; //$NON-NLS-1$
	private static final String HELLO2_DE = "German Hello"; //$NON-NLS-1$
	private static final String HELLO_EN = "Hello (Default)"; //$NON-NLS-1$
	private static final String HELLO_EN_US = "US Hello"; //$NON-NLS-1$
	private static final String HELLO_EN_GB = "Great Britain Hello"; //$NON-NLS-1$
	
	private static final String LOCALE_TEXT_ID = "localeText"; //$NON-NLS-1$
	private static final String LOCALE_TEXT0_ID = "localeText0"; //$NON-NLS-1$
	private static final String LOCALE_TEXT1_ID = "localeText1"; //$NON-NLS-1$
	private static final String LOCALE_TEXT2_ID = "localeText2"; //$NON-NLS-1$
	private static final String LOCALE_TEXT3_ID = "localeText3"; //$NON-NLS-1$
	private static final String FVIEW_ID = "fviewid"; //$NON-NLS-1$
	

	private static final String DEFAUT_BUNDLE_NAME = "demo.Messages"; //$NON-NLS-1$
	private static final String DEFAUT_MESSAGE_KEY = "hello_message"; //$NON-NLS-1$
	private String[] javaSources;
	
	public TestFViewLocaleAttribute_JBIDE5218(String name) {
		super(name);
	}

	/**
	 * Tests that the dafault locale is applied by default, 
	 * f:view has no locale attribute in this case.
	 * 
	 * @throws Throwable
	 */
	public void testDefaultLocale() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_I18N_PROJECT_NAME, DEFAULT_LOCALE_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		closeEditors();
	}
	
	
	/**
	 * The default locale is defined, 
	 * f:view has a locale attribute defined also, 
	 * The default locale in this case should take an advantage.
	 * 
	 * @throws Throwable
	 */
	public void testLocaleAttributeWithDefaultLocale() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_I18N_PROJECT_NAME,
				LOCALE_ATTRIBUTE_WITH_DEFAULT_LOCALE_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		closeEditors();
	}
	
	/**
	 * f:view has a locale attribute defined,
	 * which should be applied. 
	 * Default locale is empty in this case.
	 * 
	 * @throws Throwable
	 */
	public void testLocaleAttribute() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_20_PROJECT_NAME, LOCALE_ATTRIBUTE_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be should be in 'de' locale", HELLO2_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		closeEditors();
	}
	
	/**
	 * If there are several f:views on the page.
	 * Only the last f:view one should be applied on server, 
	 * but each f:view should have its own locale.
	 * 
	 * @throws Throwable
	 */
	public void testSeveralFViewsWithLocales() throws Throwable {
		
		IFile file =
			(IFile) TestUtil.getComponentPath(SEVERAL_FVIEWS_PAGE, JsfAllTests.IMPORT_JSF_20_PROJECT_NAME);
		assertNotNull("Could not open specified file."		//$NON-NLS-1$
				+ " componentPage = " + SEVERAL_FVIEWS_PAGE			//$NON-NLS-1$
				+ ";projectName = " + JsfAllTests.IMPORT_JSF_20_PROJECT_NAME, file);	//$NON-NLS-1$
		IEditorInput input = new FileEditorInput(file);
		assertNotNull("Editor input is null", input);		//$NON-NLS-1$
		 /*
		  * open and get the editor
		  */
		JSPMultiPageEditor part = openEditor(input);
		VpeController controller = TestUtil.getVpeController(part);
		
		javaSources = EclipseResourceUtil.getJavaProjectSrcLocations(file.getProject());
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT0_ID);
		String localizedText = getLocalizedText(localeText);
		checkLocaleStrings(file, "en", localizedText); //$NON-NLS-1$
		
		localeText = doc.getElementById(LOCALE_TEXT1_ID);
		localizedText = getLocalizedText(localeText);
		checkLocaleStrings(file, "de", localizedText); //$NON-NLS-1$
		
		localeText = doc.getElementById(LOCALE_TEXT2_ID);
		localizedText = getLocalizedText(localeText);
		/*
		 * f:view will use default locale if nothing is specified.
		 */
		checkLocaleStrings(file, "en_US", localizedText); //$NON-NLS-1$
		
		localeText = doc.getElementById(LOCALE_TEXT_ID);
		localizedText = getLocalizedText(localeText);
		checkLocaleStrings(file, "en_GB", localizedText); //$NON-NLS-1$
		
		closeEditors();
	}

	/**
	 * After the locale attribute value has been changed and 
	 * Refresh button is clicked - the correct locale should be applied,
	 * bundle messages should be updated and showed in the correct locale.
	 * 
	 * @throws Throwable
	 */
	public void testChangeLocaleAndRefresh() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_20_PROJECT_NAME,
				CHANGE_LOCALE_AND_REFRESH_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO2_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Change the locale
		 */
		Element fViewElement = controller.getSourceBuilder().getSourceDocument().getElementById(FVIEW_ID);
		assertTrue("Previous locale should be 'de'", "de".equalsIgnoreCase(fViewElement.getAttribute("locale"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		fViewElement.setAttribute("locale", "en_GB"); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Wait until new value is applied and children are refreshed.
		 */
		TestUtil.delay(500);
		TestUtil.waitForIdle();
		assertTrue("Current locale should be 'en_GB'", "en_GB".equalsIgnoreCase(fViewElement.getAttribute("locale"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doc = controller.getXulRunnerEditor().getDOMDocument();
		localeText = doc.getElementById(LOCALE_TEXT_ID);
		localizedText = getLocalizedText(localeText);
		/*
		 * Check the new localized message.
		 */
		assertTrue("Text is '"+localizedText+"', but should be in 'en_GB' locale", HELLO_EN_GB.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		closeEditors();
	}
	
	public void testNoDefaultLocaleForLocaleAttribute() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_LOCALES_PROJECT_NAME,
				NO_DEFLOC_ATTRIBUTE_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO2_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		closeEditors();
	}
	
	public void _testNoDefaultLocaleForSeveralFViews() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_LOCALES_PROJECT_NAME, NO_DEFLOC_SEVERAL_FVIEWS_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT0_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$

		localeText = doc.getElementById(LOCALE_TEXT1_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'default' locale", HELLO_EN.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT2_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO2_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT3_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_GB' locale", HELLO_EN_GB.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		closeEditors();
	}
	
	public void testNoDefaultLocaleForChangeAndRefresh() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_LOCALES_PROJECT_NAME,
				NO_DEFLOC_CHANGE_REFRESH_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO2_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Change the locale
		 */
		Element fViewElement = controller.getSourceBuilder().getSourceDocument().getElementById(FVIEW_ID);
		int offset = controller.getSourceBuilder().getPosition(fViewElement,0,false);
		assertTrue("Previous locale should be 'de'", "de".equalsIgnoreCase(fViewElement.getAttribute("locale"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		IRegion reg = new FindReplaceDocumentAdapter(
				controller.getSourceBuilder().getStructuredTextViewer().getDocument())
					.find(offset, "de", true, true, false, false);
		controller.getSourceBuilder().getStructuredTextViewer().getDocument().replace(reg.getOffset(), reg.getLength(), "en_GB");
		/*
		 * Wait until new value is applied and children are refreshed.
		 * Wait while all deferred events are processed
		 */
		while(Display.getCurrent().readAndDispatch());
		/*
		 * Wait while all jobs including started through deferred events are ended
		 */
		JobUtils.delay(VpeController.DEFAULT_UPDATE_DELAY_TIME*4);
		TestUtil.waitForIdle();
		fViewElement = controller.getSourceBuilder().getSourceDocument().getElementById(FVIEW_ID);
		assertTrue("Current locale should be 'en_GB'", "en_GB".equalsIgnoreCase(fViewElement.getAttribute("locale"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		doc = controller.getXulRunnerEditor().getDOMDocument();
		localeText = doc.getElementById(LOCALE_TEXT_ID);
		localizedText = getLocalizedText(localeText);

		/*
		 * Check the new localized message.
		 */
		assertTrue("Text is '"+localizedText+"', but should be in 'en_GB' locale", HELLO_EN_GB.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		closeEditors();
	}
	
	public void testNoDefaultLocaleForSingleFLoadBundle() throws Throwable {
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_LOCALES_PROJECT_NAME, NO_DEFLOC_ONE_LOAD_BUNDLE_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT0_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$

		localeText = doc.getElementById(LOCALE_TEXT1_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT2_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT3_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_US' locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		closeEditors();
	}
	
	/**
	 * Short version of {@link #checkLocaleStrings(IFile, String, String, String, String)}
	 * <p>
	 * Uses default values for bundle name and message key.
	 */
	private void checkLocaleStrings(IFile openedFile, String localeName, String currentText) {
		checkLocaleStrings(openedFile, localeName, DEFAUT_BUNDLE_NAME,
				DEFAUT_MESSAGE_KEY, currentText);
	}
	
	/**
	 * Loads a message from resource bundle and
	 * compares it to the specified one.
	 * <p>
	 * Fixes difference in JVM 5 and 6 bundles loading. 
	 * 
	 * @param openedFile file opened in the editor
	 * @param localeName string code of the locale
	 * @param bundleName the name of the bundle
	 * @param bundleKey message key
	 * @param currentText compared string
	 */
	private void checkLocaleStrings(IFile openedFile, String localeName,
			String bundleName, String bundleKey, String currentText) {
		/*
		 * javaSources should be initialized before call.
		 */
		URL[] urls = new URL[0];
		try {
			if (javaSources != null) {
				File tempFile;
				urls = new URL[javaSources.length];
				for (int i = 0; i < javaSources.length; ++i) {
					try {
						tempFile = new File(javaSources[i]).getCanonicalFile();
						urls[i] = tempFile.toURL();
					} catch (IOException ioe) {
						VpePlugin.reportProblem(ioe);
					}
				}

			}
		} catch (MissingResourceException ex) {
			VpePlugin.getDefault().logError(
					"Project source folder is missing!", ex); //$NON-NLS-1$
		}
		ClassLoader classLoader = new URLClassLoader(urls, ClassLoader
				.getSystemClassLoader());

		Locale locale = ComponentUtil.createLocale(localeName);
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName,
				locale, classLoader);
		String bundleText = bundle.getString(bundleKey);
		assertNotNull(bundleText);
		assertTrue(
				"Text is '" + currentText + "', but should be in '"//$NON-NLS-1$ //$NON-NLS-2$
						+ localeName
						+ "' locale, bundle's value is '" + bundleText + "'", bundleText.equalsIgnoreCase(currentText) //$NON-NLS-1$ //$NON-NLS-2$ 
		);
	}
	
	/**
	 * Gets the text value from the container.
	 * Container should be a simple tag like div or span.
	 * The text node in the VPE is initially wrapped in a span element, 
	 * thus to get its value two child elements should be skipped.
	 * 
	 * @param textContainer - the tag with text
	 * @return localized by VPE string
	 */
	private String getLocalizedText(nsIDOMElement textContainer) {
		String text = ""; //$NON-NLS-1$
		if ((textContainer != null) && (textContainer.getFirstChild() != null)
				&& (textContainer.getFirstChild().getFirstChild() != null)
				&& HTML.TAG_SPAN.equalsIgnoreCase(textContainer.getFirstChild()
						.getNodeName())) {
			text = textContainer.getFirstChild().getFirstChild().getNodeValue()
					.trim();
		}
		return text;
	}
	
}
