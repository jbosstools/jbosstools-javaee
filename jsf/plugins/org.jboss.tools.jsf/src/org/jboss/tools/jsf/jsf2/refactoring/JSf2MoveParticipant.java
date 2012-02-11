/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsf2.refactoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
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
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;

/**
 * 
 * @author yzhishko
 *
 */

public class JSf2MoveParticipant extends MoveParticipant {

	private IProject project;
	private Map<String, String> urisMap;
	private static boolean isMoveContainer = true;

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (project != null) {
			return RefactoringChangesFactory.createRenameURIChanges(project, urisMap);
		}
		return null;
	}

	@Override
	public String getName() {
		return JSFUIMessages.Refactoring_JSF_2_resources;
	}

	@Override
	protected boolean initialize(Object element) {
		if(!(element instanceof IContainer) && !(element instanceof IFile))
			return false;
		
		IContainer container = null;
		isMoveContainer = true;
		if(element instanceof IFile){
			IFile file = (IFile)element;
			
			container = file.getParent();
			isMoveContainer = false;
		}else
			container = (IContainer) element;
		if (checkResourceContainerPath(container.getFullPath())) {
			Object object = getArguments().getDestination();
			if (object instanceof IContainer) {
				if (container.getProject() != ((IContainer) object).getProject()) {
					return false;
				}
				if (checkDistContainerPath(((IContainer) object).getFullPath())) {
					project = container.getProject();
					urisMap = invokePossibleURIs(container, (IContainer) object);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkDistContainerPath(IPath fullPath) {
		String[] segments = fullPath.segments();
		if (segments.length > 2) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}
	
	public static Map<String, String> invokePossibleURIs(IContainer srcContainer, IContainer distContainer){
		return invokePossibleURIs(srcContainer, distContainer.getFullPath(), isMoveContainer);
	}

	public static Map<String, String> invokePossibleURIs(IContainer srcContainer, IPath distPath, boolean isMoveContainer) {
		Map<String, String> urisMap;
		String newFirstURIPart = createJSF2URIFromPath(distPath);
		String oldFirstURIPart = createJSF2URIFromPath(srcContainer.getFullPath());
		if(isMoveContainer)
			oldFirstURIPart = oldFirstURIPart.substring(0, oldFirstURIPart
				.lastIndexOf('/'));
		Set<String> oldURIs = new HashSet<String>();
		invokeOldPossibleURIs(srcContainer, oldURIs);
		urisMap = new HashMap<String, String>();
		for (String oldURI : oldURIs) {
			urisMap.put(oldURI, newFirstURIPart
					+ oldURI.replaceFirst(oldFirstURIPart, "")); //$NON-NLS-1$
		}
		return urisMap;
	}
	
	private static void invokeOldPossibleURIs(IContainer srcContainer, Set<String> uris) {
		uris.add(createJSF2URIFromPath(srcContainer.getFullPath()));
		try {
			IResource[] children = srcContainer.members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					if (children[i] instanceof IContainer) {
						invokeOldPossibleURIs((IContainer) children[i], uris);
					}
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	public static boolean checkResourceContainerPath(IPath path) {
		String[] segments = path.segments();
		if (segments.length > 3) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	private static String createJSF2URIFromPath(IPath path) {
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
