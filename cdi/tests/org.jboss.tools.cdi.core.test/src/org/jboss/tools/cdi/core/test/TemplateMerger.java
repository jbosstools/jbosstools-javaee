/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import org.jboss.tools.tests.ImportProvider;
import org.osgi.framework.Bundle;

/**
 * @author Viacheslav Kabanovich
 */
public class TemplateMerger {

	/**
	 * Creates ImportOperation that adds resources to containerPath
	 * from updatingTemplateLocation in bundle, excluding files with same paths
	 * existing in currentTemplateLocation in bundle.
	 * 
	 * @param containerPath - path to container in the workspace to be updated
	 * @param bundle - root for templates
	 * @param currentTemplateLocation - templates that should not be updated 
	 * @param updatingTemplateLocation - templates that should be copied into container if they are not in currentTemplateLocation
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 * @throws InvocationTargetException
	 */
	public static ImportOperation createImportOperation(IPath containerPath, Bundle bundle, String currentTemplateLocation, String updatingTemplateLocation) throws IOException, CoreException, InvocationTargetException {
		 IOverwriteQuery overwrite = new IOverwriteQuery() {
			public String queryOverwrite(String pathString) {
				return ALL;
			}
		};

		ProviderWrapper currentProvider = new ProviderWrapper(bundle, currentTemplateLocation);
		ProviderWrapper updatingProvider = new ProviderWrapper(bundle, updatingTemplateLocation);
		MergingProvider mergingProvider = new MergingProvider(
				currentProvider.getStructureProvider(), currentProvider.getStructureRootHandle(), 
				updatingProvider.getStructureProvider(), updatingProvider.getStructureRootHandle());

		ImportOperation result = new ImportOperation(
				containerPath, 
				mergingProvider.source, 
				mergingProvider, 
				overwrite);
		// import files just to project folder ( without old structure )
		result.setCreateContainerStructure(false);
		
		return result;
	}

	/**
	 * Helper that creates for templLocation in bundle its structure provider 
	 * and that structure's root handle object.
	 *
	 */
	public static class ProviderWrapper {
		private IImportStructureProvider provider = null;
		private Object source = null;
		
		public ProviderWrapper(Bundle bundle, String templLocation) throws IOException, CoreException, InvocationTargetException {
			String path = FileLocator.resolve(bundle.getEntry(templLocation)).getFile();
			String protocol = FileLocator.resolve(bundle.getEntry(templLocation)).getProtocol();
			if("jar".equals(protocol)) {
				String pathToZip = path.substring(0,path.indexOf("!"));
				String zipEntryName = path.substring(path.indexOf("!") + 2, path.length());
				pathToZip = pathToZip.substring("file:".length());
				ZipFileStructureProvider zipStrProvider = new ZipFileStructureProvider(new ZipFile(pathToZip));
				provider = zipStrProvider;
				source = getZipEntry(zipStrProvider, zipEntryName);
			} else {
				ImportProvider importProvider = new ImportProvider();
				provider = importProvider;
				source = new File(path);

				// need to remove from imported project "svn" files
				List<String> unimportedFiles = new ArrayList<String>();
				unimportedFiles.add(".svn"); //$NON-NLS-1$

				importProvider.setUnimportedFiles(unimportedFiles);
			}
		}

		/**
		 * Returns computed structure provider.
		 * 
		 * @return
		 */
		public IImportStructureProvider getStructureProvider() {
			return provider;
		}

		/**
		 * Returns root handle object for the structure provider.
		 * @return
		 */
		public Object getStructureRootHandle() {
			return source;
		}
	}

	private static ZipEntry getZipEntry(ZipFileStructureProvider zipStrProvider, String zipEntryName) {
		String[] entries = zipEntryName.split("/");
		ZipEntry parent = zipStrProvider.getRoot();
		for (String string : entries) {
			List<?> children = zipStrProvider.getChildren(parent);
			for (Object object : children) {
				ZipEntry current = (ZipEntry)object;
				String name = parent== zipStrProvider.getRoot()? string + "/": parent.getName() + string + "/";
				if(name.equals(current.getName())) {
					parent = current;
					break;
				}
			}
			
		}
		return parent;
	}

	/**
	 * Provider that returns structure of updatingSource obtained with updatingProvider
	 * minus structure of currentSource obtained with currentProvider.
	 *
	 */
	public static class MergingProvider implements IImportStructureProvider {
		private static class CombinedSource {
			Object currentSource;
			Object updatingSource;
			CombinedSource(Object currentSource, Object updatingSource) {
				this.updatingSource = updatingSource;
				this.currentSource = currentSource;
			}
		}
		IImportStructureProvider currentProvider;
		IImportStructureProvider updatingProvider;
		CombinedSource source;

		public MergingProvider(IImportStructureProvider currentProvider, Object currentSource,
				IImportStructureProvider updatingProvider, Object updatingSource) {
			this.updatingProvider = updatingProvider;
			this.currentProvider = currentProvider;
			source = new CombinedSource(currentSource, updatingSource);
		}

		@Override
		public List<Object> getChildren(Object element) {
			if(element instanceof CombinedSource) {
				List<Object> result = new ArrayList<Object>();
				Map<String, Object> map12 = new HashMap<String, Object>();
				if(((CombinedSource)element).currentSource != null) {
					for (Object c12: currentProvider.getChildren(((CombinedSource)element).currentSource)) {
						map12.put(currentProvider.getLabel(c12), c12);
					}
				}
				for (Object c11: updatingProvider.getChildren(((CombinedSource)element).updatingSource)) {
					String label = updatingProvider.getLabel(c11);
					Object c12 = map12.get(label);
					if(c12 == null || (currentProvider.isFolder(c12) && !"lib".equals(label))) {
						result.add(new CombinedSource(c12, c11));
					} else {
//						System.out.println("Filtered out: " + updatingProvider.getFullPath(c11));
					}
				}				
				return result;
			}
			return null;
		}

		@Override
		public InputStream getContents(Object element) {
			if(element instanceof CombinedSource) {
				return updatingProvider.getContents(((CombinedSource)element).updatingSource);
			}
			return null;
		}

		@Override
		public String getFullPath(Object element) {
			if(element instanceof CombinedSource) {
				return updatingProvider.getFullPath(((CombinedSource)element).updatingSource);
			}
			return null;
		}

		@Override
		public String getLabel(Object element) {
			if(element instanceof CombinedSource) {
				return updatingProvider.getLabel(((CombinedSource)element).updatingSource);
			}
			return null;
		}

		@Override
		public boolean isFolder(Object element) {
			if(element instanceof CombinedSource) {
				return updatingProvider.isFolder(((CombinedSource)element).updatingSource);
			}
			return false;
		}
		
	}
}
