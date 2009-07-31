/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.model.FileFacesConfigImpl;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class BundleBasenameHyperlink extends ClassHyperlink {
	private static final String FILESYSTEMS = "/FileSystems/"; //$NON-NLS-1$
	private static final String LIB = "/lib-"; //$NON-NLS-1$
	private static final String SEPARATOR = "/"; //$NON-NLS-1$
	
	protected void doHyperlink(IRegion region) {
		try {
			String fileName = getBundleBasename(region);
			XModelObject mo = getXModelObjectToOpen(fileName);
			if (mo != null) {
				// Open XModelObject in editor
				FindObjectHelper.findModelObject(mo, FindObjectHelper.IN_EDITOR_ONLY);
			} else {
				IFile fileToOpen = getFileToOpen(fileName, "properties"); //$NON-NLS-1$
				if (fileToOpen != null) {
					IWorkbenchPage workbenchPage = JSFExtensionsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(workbenchPage,fileToOpen,true);
				} else {
					super.doHyperlink(region);
				}
			}
		} catch (CoreException x) {
			// could not open editor
			openFileFailed();
		}
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
		
		XModelObject rbObjects = jsfProjectsRoot.getChildByPath("Resource Bundles"); //$NON-NLS-1$
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
	
	private XModelObject getXModelObjectToOpen(String fileName) {
		// Search thru the XModelObject for Faces Config
		String baseLocation = getBaseLocation();
		if (baseLocation == null)
			return null;
		
		int index = baseLocation.indexOf(FILESYSTEMS);
		if (index == -1)
			return null;
		
		String projectName = baseLocation.substring(1, index);
		String path = baseLocation.substring(index + 1);

		IProject project = null;
		try {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		} catch (Throwable x) {
			return null;
		}
		IModelNature modelNature = EclipseResourceUtil.getModelNature(project);
		if (modelNature == null || modelNature.getModel() == null)
			return null;
		
		XModelObject xmo = modelNature.getModel().getByPath(path);
		
		if (xmo instanceof FileFacesConfigImpl) {
			XModelObject fcObject = xmo;
		
			String[] orderedLocales = getOrderedLocales(fcObject);
			XModelObject[] bundles = getBundles(fcObject);
			for (int l = 0; orderedLocales != null && l < orderedLocales.length; l++) {
				String name = fileName + (orderedLocales[l].length() == 0 ? "" : //$NON-NLS-1$
								"_" + orderedLocales[l]); //$NON-NLS-1$

				for (int i = 0; bundles != null && i < bundles.length; i++) {
					String bundleName = XModelObjectLoaderUtil.getResourcePath(bundles[i]);

					if (bundleName.equals(name)) {
							return bundles[i];
					}
				}
			}
		}
		
		return null;
	}
	
	private IFile getFileToOpen(String fileName, String fileExt) {
		if (fileName == null)
			return null;
		
		String[] orderedLocales = getOrderedLocales();
		String[] bundles = getBundles();

		for (int l = 0; orderedLocales != null && l < orderedLocales.length; l++) {
			String name = fileName + (orderedLocales[l].length() == 0 ? "" : //$NON-NLS-1$
							"_" + orderedLocales[l]); //$NON-NLS-1$

			for (int i = 0; bundles != null && i < bundles.length; i++) {
				if (bundles[i].equals(name)) {
					IFile file = findFile(name.replace('.','/')+
			                (fileExt != null ? "." + fileExt : "")); //$NON-NLS-1$ //$NON-NLS-2$
					if (file != null)
						return file;
				}
			}
		}
		return null;
	}

	private IFile findFile(String name) {
		IFile documentFile = getFile();
		try {	
			IProject project = documentFile.getProject();
		
			if(project == null || !project.isOpen()) return null;
			if(!project.hasNature(JavaCore.NATURE_ID)) return null;
			IJavaProject javaProject = JavaCore.create(project);		
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				if(es[i].getEntryKind() != IClasspathEntry.CPE_SOURCE) continue;
				IFile file = (IFile)project.getFile(es[i].getPath().removeFirstSegments(1) + "/" + name); //$NON-NLS-1$
				if(file != null && file.exists()) return file;
			}
			return null;
		} catch (CoreException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}
	}
	
	private IFile getFileToOpenOld(String fileName, String fileExt) {
		IFile documentFile = getFile();
		try {	
			IProject project = documentFile.getProject();
			
			String name = fileName.replace('.','/')+ (fileExt != null ? "." + fileExt : ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			if(project == null || !project.isOpen()) return null;
			if(!project.hasNature(JavaCore.NATURE_ID)) return null;
			IJavaProject javaProject = JavaCore.create(project);		
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				if(es[i].getEntryKind() != IClasspathEntry.CPE_SOURCE) continue;
				IFile file = (IFile)project.getFile(es[i].getPath().removeFirstSegments(1) + "/" + name); //$NON-NLS-1$
				if(file != null && file.exists()) return file;
			}
			return null;
		} catch (CoreException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}

	}

	IRegion fLastRegion = null;
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = getRegion(offset);
		return fLastRegion;
	}

	public IRegion getRegion(int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);

			if (start > offset || end < offset) return null;

			String text = getDocument().get(start, end - start);
			StringBuffer sb = new StringBuffer(text);

			//find start and end of path property
			int bStart = 0;
			int bEnd = text.length() - 1;

			while (bStart < bEnd && (Character.isWhitespace(sb.charAt(bStart)) 
					|| sb.charAt(bStart) == '\"' || sb.charAt(bStart) == '\"')) { 
				bStart++;
			}
			while (bEnd > bStart && (Character.isWhitespace(sb.charAt(bEnd)) 
					|| sb.charAt(bEnd) == '\"' || sb.charAt(bEnd) == '\"')) { 
				bEnd--;
			}
			bEnd++;

			final int propStart = bStart + start;
			final int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
	
			return new Region(propStart,propLength);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
		}
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