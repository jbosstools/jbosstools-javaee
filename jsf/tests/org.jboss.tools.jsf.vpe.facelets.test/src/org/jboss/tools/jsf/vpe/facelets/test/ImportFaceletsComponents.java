package org.jboss.tools.jsf.vpe.facelets.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
 * @author dsakovich@exadel.com
 * 
 */
public class ImportFaceletsComponents {
    private static final String PROJECT_NAME = "faceletsTest"; // $NON-NLS-1$
    private static final String COMPONENTS_PATH = "WebContent/pages"; // $NON-NLS-1$

    @SuppressWarnings("restriction")
    static void importFaceletsPages(String path) {
	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
		PROJECT_NAME);
	ZipLeveledStructureProvider zipStructureProvider;
	try {
	    zipStructureProvider = new ZipLeveledStructureProvider(new ZipFile(
		    path));

	    IOverwriteQuery overwrite = new IOverwriteQuery() {
		public String queryOverwrite(String pathString) {
		    return ALL;
		}
	    };

	    ImportOperation importOp = new ImportOperation(project
		    .getFullPath(), zipStructureProvider.getRoot(),
		    zipStructureProvider, overwrite);

	    importOp.setContext(PlatformUI.getWorkbench()
		    .getActiveWorkbenchWindow().getShell());

	    importOp.run(new NullProgressMonitor());
	} catch (InvocationTargetException ite) {
	    FaceletsTestPlugin.getPluginLog().logError(ite.getCause());
	} catch (InterruptedException ie) {
	    FaceletsTestPlugin.getPluginLog().logError(ie);
	} catch (IOException e) {
	    FaceletsTestPlugin.getPluginLog().logError(e);
	}
    }

    /**
     * 
     * @return
     * @throws CoreException
     */
    static IPath getComponentPath(String componentPage) throws CoreException {
	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
		PROJECT_NAME);
	if (project != null) {
	    IResource resource = project.getFolder(COMPONENTS_PATH).findMember(
		    componentPage);
	    if (resource != null) {
		return resource.getFullPath();
	    }

	}

	return null;
    }

    /**
     * 
     * @throws CoreException
     */
    static void removeProject() throws CoreException {
	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
		PROJECT_NAME);
	if (project != null) {
	    project.delete(IResource.ALWAYS_DELETE_PROJECT_CONTENT,
		    new NullProgressMonitor());
	}
    }
}
