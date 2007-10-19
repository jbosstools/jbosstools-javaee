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
package org.jboss.tools.jsf.vpe.richfaces.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.ZipLeveledStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * Class for importing project from jar file
 * 
 * @author dsakharov@exadel.com,amakhtadui@exadel.com
 * 
 */
public class ImportRichFacesComponents {
    private static final String PROJECT_NAME = "RichFacesTest"; // $NON-NLS-1$
    private static final String COMPONENTS_PATH = "WebContent/pages"; // $NON-NLS-1$

    @SuppressWarnings("restriction")
	public static boolean importRichFacesPages(String path) {
		boolean result = false;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(PROJECT_NAME);
		ZipLeveledStructureProvider zipStructureProvider;
		try {
			zipStructureProvider = 
				new ZipLeveledStructureProvider(new ZipFile(path));

			IOverwriteQuery overwrite = new IOverwriteQuery() {
				public String queryOverwrite(String pathString) {
					return ALL;
				}
			};

			ImportOperation importOp = new ImportOperation(
												project.getFullPath(),
												zipStructureProvider.getRoot(),
												zipStructureProvider,
												overwrite);

			importOp.setContext(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

			importOp.run(new NullProgressMonitor());
			result = true;

		} catch (InvocationTargetException ite) {
			Activator.getPluginLog().logError(ite.getCause());
		} catch (InterruptedException ie) {
			Activator.getPluginLog().logError(ie);
		} catch (IOException e) {
			Activator.getPluginLog().logError(e);
		}
		return result;
	}

    /**
	 * 
	 * @return
	 * @throws CoreException
	 */
    public static Collection<IPath> getComponentsPaths() throws CoreException {
		Collection<IPath> result = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(PROJECT_NAME);
		if (project != null) {
			IFolder folder = project.getFolder(COMPONENTS_PATH);
			IResource[] resources = folder.members();
			if (resources != null && resources.length > 0) {
				result = new ArrayList<IPath>(resources.length);
				for (IResource res : resources) {
					result.add(res.getFullPath());
				}
			}
		}
		return result;
	}

    /**
     * 
     * @throws CoreException
     */
    public static void removeProject() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(PROJECT_NAME);
		project.delete(IResource.ALWAYS_DELETE_PROJECT_CONTENT,
				new NullProgressMonitor());
	}
}
