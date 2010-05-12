package org.jboss.tools.jsf.jsf2.refactoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class JSf2MoveParticipant extends MoveParticipant {

	private IProject project;
	private Map<String, String> urisMap;

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (project != null) {
			JSF2ComponentModelManager.getManager().renameURIs(project, urisMap);
		}
		return null;
	}

	@Override
	public String getName() {
		return JSFUIMessages.Refactoring_JSF_2_resources;
	}

	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			if (checkResourceFolderPath(folder.getFullPath())) {
				Object object = getArguments().getDestination();
				if (object instanceof IFolder) {
					if (folder.getProject() != ((IFolder) object).getProject()) {
						return false;
					}
					if (checkDistFolderPath(((IFolder) object).getFullPath())) {
						project = folder.getProject();
						invokePossibleURIs(folder, (IFolder) object);
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkDistFolderPath(IPath fullPath) {
		String[] segments = fullPath.segments();
		if (segments.length > 2) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	private void invokePossibleURIs(IFolder srcFolder, IFolder distFolder) {
		String newFirstURIPart = createJSF2URIFromPath(distFolder.getFullPath());
		String oldFirstURIPart = createJSF2URIFromPath(srcFolder.getFullPath());
		oldFirstURIPart = oldFirstURIPart.substring(0, oldFirstURIPart
				.lastIndexOf('/'));
		Set<String> oldURIs = new HashSet<String>();
		invokeOldPossibleURIs(srcFolder, oldURIs);
		urisMap = new HashMap<String, String>();
		for (String oldURI : oldURIs) {
			urisMap.put(oldURI, newFirstURIPart
					+ oldURI.replaceFirst(oldFirstURIPart, "")); //$NON-NLS-1$
		}
	}

	private void invokeOldPossibleURIs(IFolder srcFolder, Set<String> uris) {
		uris.add(createJSF2URIFromPath(srcFolder.getFullPath()));
		try {
			IResource[] children = srcFolder.members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					if (children[i] instanceof IFolder) {
						invokeOldPossibleURIs((IFolder) children[i], uris);
					}
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	private boolean checkResourceFolderPath(IPath path) {
		String[] segments = path.segments();
		if (segments.length > 3) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	private String createJSF2URIFromPath(IPath path) {
		StringBuilder uri = new StringBuilder(""); //$NON-NLS-1$
		String[] segments = path.segments();
		if (segments.length > 3) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				for (int i = 3; i < segments.length; i++) {
					uri.append("/" + segments[i]); //$NON-NLS-1$
				}
			}
		}
		uri.insert(0, JSF2ResourceUtil.JSF2_URI_PREFIX);
		return uri.toString();
	}

}
