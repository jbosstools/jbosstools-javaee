/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class ProjectContentProvider extends AbstractSeamContentProvider {

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IJavaProject) {
			parentElement = ((IJavaProject)parentElement).getProject();
		}
		if(parentElement instanceof IProject) {
			ISeamProject p = SeamCorePlugin.getSeamProject((IProject)parentElement, false);
			if (p == null) return null;
			if(!processed.contains(p)) {
				processed.add(p);
				p.addSeamProjectListener(this);
			}
			return new Object[]{p};
		} else {
			return super.getChildren(parentElement);
		}
	}

	public Object getParent(Object element) {
		if(element instanceof ISeamProject) {
			return ((ISeamProject)element).getProject();
		} else {
			return super.getParent(element);
		}
	}

}
