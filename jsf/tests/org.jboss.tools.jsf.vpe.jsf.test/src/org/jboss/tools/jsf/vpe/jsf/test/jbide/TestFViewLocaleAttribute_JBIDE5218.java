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

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
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
		VpeController controller = openInVpe(
				JsfAllTests.IMPORT_JSF_20_PROJECT_NAME, SEVERAL_FVIEWS_PAGE);
		nsIDOMDocument doc = controller.getXulRunnerEditor().getDOMDocument();
		
		nsIDOMElement localeText = doc.getElementById(LOCALE_TEXT0_ID);
		String localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en' locale", HELLO_EN.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$

		localeText = doc.getElementById(LOCALE_TEXT1_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'de' locale", HELLO2_DE.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT2_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in default locale", HELLO_EN_US.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
		localeText = doc.getElementById(LOCALE_TEXT_ID);
		localizedText = getLocalizedText(localeText);
		assertTrue("Text is '"+localizedText+"', but should be in 'en_GB' locale", HELLO_EN_GB.equalsIgnoreCase(localizedText)); //$NON-NLS-1$ //$NON-NLS-2$
		
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
		assertTrue("Previous locale should be 'de'", "de".equalsIgnoreCase(fViewElement.getAttribute("locale"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		fViewElement.setAttribute("locale", "en_GB"); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Wait until new value is applied and children are refreshed.
		 */
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
