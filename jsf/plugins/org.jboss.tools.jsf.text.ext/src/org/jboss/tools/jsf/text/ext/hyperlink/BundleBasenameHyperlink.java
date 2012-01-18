/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.model.FileFacesConfigImpl;
import org.jboss.tools.jsf.model.pv.JSFProjectTreeConstants;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class BundleBasenameHyperlink extends XModelBasedHyperlink {
	protected String getRequestMethod() {
		return WebPromptingProvider.JSF_OPEN_BUNDLE;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String value = getBundleBasename(region);
		value = (value == null? "" : value); //$NON-NLS-1$
		p.setProperty(WebPromptingProvider.BUNDLE, value);

		String[] locales = getOrderedLocales();
		if(locales != null && locales.length > 0) {
			value = locales[0];
			if (value != null) {
				p.setProperty(WebPromptingProvider.LOCALE, value);
			}
		}

		return p;
	}
	
	private String getBundleBasename(IRegion region) {
		if(region == null || getDocument() == null) return null;
		try {
			return getDocument().get(region.getOffset(), region.getLength());
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}
	}

	private XModelObject[] getBundles(XModelObject fcObject) {
		if (fcObject == null)
			return null;
		
		XModel fcObjectModel = fcObject.getModel();
		if (fcObjectModel == null)
			return null;
		
		JSFProjectsRoot jsfProjectsRoot = JSFProjectsTree.getProjectsRoot(fcObjectModel);
		if (jsfProjectsRoot == null)
			return null;
		
		XModelObject rbObjects = jsfProjectsRoot.getChildByPath(JSFProjectTreeConstants.RESOURCE_BUNDLES);
		if (!(rbObjects instanceof WebProjectNode))
			return null;
		
		((WebProjectNode)rbObjects).invalidate();
		
		ArrayList<XModelObject> resourceBundles = new ArrayList<XModelObject>();
		XModelObject[] bundles = ((WebProjectNode)rbObjects).getTreeChildren();
		for (int i = 0; bundles != null && i < bundles.length; i++) {
			String res = XModelObjectLoaderUtil.getResourcePath(bundles[i]);
			if (res != null) {
				resourceBundles.add(bundles[i]);
			}
		}

		return (resourceBundles.size() == 0 ? 
				null : resourceBundles.toArray(new XModelObject[0]));
		
	}
	
	private String[] getBundles() {
		XModelObject fcObject = EclipseResourceUtil.createObjectForResource(getFile());
		if (fcObject == null)
			return null;
		
		ArrayList<String> bundlesPaths = new ArrayList<String>();
		XModelObject[] bundles = getBundles(fcObject);
		for (int i = 0; bundles != null && i < bundles.length; i++) {
			String res = XModelObjectLoaderUtil.getResourcePath(bundles[i]);
			if (res != null) {
				res = res.substring(1, res.length() - 11).replace('/', '.');
				bundlesPaths.add(res);
			}
		}

		return (bundlesPaths.size() == 0 ? 
				null : bundlesPaths.toArray(new String[0]));
	}
	
	private String[] getOrderedLocales() {
		XModelObject fcObject = EclipseResourceUtil.createObjectForResource(getFile());
		if (fcObject == null)
			return null;
		
		return getOrderedLocales(fcObject); 
	}
	
	private String[] getOrderedLocales(XModelObject fcObject) {
		if (fcObject == null)
			return null;

		HashSet<String> allLocales = new HashSet<String>();
		ArrayList<String> supportedLocales = new ArrayList<String>();
		ArrayList<String> langs = new ArrayList<String>();  
		
		XModelObject lcObject = (fcObject != null ? 
				fcObject.getChildByPath("application/Locale Config") : null); //$NON-NLS-1$
		
		String defLocale = (lcObject != null ? 
				lcObject.getAttributeValue("default-locale") : null); //$NON-NLS-1$
		if (defLocale != null && defLocale.trim().length() > 0) {
			String locale = defLocale.trim().replace('-', '_');
			if (!allLocales.contains(locale)) {
				allLocales.add(locale);
				supportedLocales.add(locale); // Add locale in form <lang>_<country>
			}
			
			if (locale.indexOf('_') != -1) {
				locale = locale.replace('_', '-');
				if (!allLocales.contains(locale)) {
					allLocales.add(locale);
					supportedLocales.add(locale); // Add locale in form <lang>-<country>
				}
				
				locale = locale.substring(0, locale.indexOf('-'));
				if (!allLocales.contains(locale)) {
					allLocales.add(locale);
					supportedLocales.add(locale); // Add locale in form <lang>
				}
			}
		}

		java.util.Locale defJavaLocale = java.util.Locale.getDefault();
		if (defJavaLocale != null) { 
			if (defJavaLocale.getLanguage() != null && defJavaLocale.getLanguage().length() > 0) {
				if (defJavaLocale.getCountry() != null && defJavaLocale.getCountry().length() > 0) {

					// Add locale in form <lang>_<country>
					String locale = defJavaLocale.getLanguage() + '_' + defJavaLocale.getCountry();
					if (!allLocales.contains(locale)) {
						allLocales.add(locale);
						supportedLocales.add(locale);
					}
					// Add locale in form <lang>-<country>
					locale = defJavaLocale.getLanguage() + '-' + defJavaLocale.getCountry();
					if (!allLocales.contains(locale)) {
						allLocales.add(locale);
						supportedLocales.add(locale);
					}
				}
				
				String locale = defJavaLocale.getLanguage();
				if (!allLocales.contains(locale)) {
					allLocales.add(locale);
					langs.add(locale); // Add locale in form <lang> to the langs
				}
			}
		}
		
		XModelObject[] lcChildren = (lcObject != null ? 
				lcObject.getChildren() : null);
		
		for (int i = 0; lcChildren != null && i < lcChildren.length; i++) {
			String supLocale = lcChildren[i].getAttributeValue("supported-locale"); //$NON-NLS-1$
			if (supLocale != null && supLocale.trim().length() > 0) {
				String locale = supLocale.trim().replace('-', '_');

				if (!allLocales.contains(locale)) {
					allLocales.add(locale);
					supportedLocales.add(locale); // Add locale in form <lang>_<country>
				}
				
				if (locale.indexOf('_') != -1) {
					locale = locale.replace('_', '-');
					if (!allLocales.contains(locale)) {
						allLocales.add(locale);
						supportedLocales.add(locale); // Add locale in form <lang>-<country>
					}
					
					locale = locale.substring(0, locale.indexOf('-'));
					if (!allLocales.contains(locale)) {
						allLocales.add(locale);
						langs.add(locale); // Add locale in form <lang> to the langs
					}
				}
			}
		}
		
		// Add all the collected locales in form <lang> and an empty locale
		supportedLocales.addAll(langs);
		supportedLocales.add(""); //$NON-NLS-1$
		return supportedLocales.toArray(new String[0]);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String baseName = getBundleBasename(fLastRegion);
		if (baseName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.Bundle);
		
		return MessageFormat.format(Messages.OpenBundle, baseName);
	}

}