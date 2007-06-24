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
package org.jboss.tools.struts.model.handlers.refactoring;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.ltk.core.refactoring.*;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.ant.model.FileAntImpl;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class StrutsRenamePathDiagramChange extends CompositeChange {
	
	static String getName(XModelObject config) {
		String n = config.getAttributeValue("name"); //$NON-NLS-1$
		if(config instanceof FileAntImpl) {
			n = FileAntImpl.toFileName(config);
		}
		IResource r = EclipseResourceUtil.getResource(config);
		if(r != null && r.getParent() != null) {
			n += " - " + r.getParent().getFullPath(); //$NON-NLS-1$
		}
		return n;
	}
	
	public StrutsRenamePathDiagramChange(XModelObject config, XModelObject[] pages) {
		super(getName(config));
		for (int i = 0; i < pages.length; i++) {
			final XModelObject page = pages[i];
			add(new Change() {
				public String getName() {
					return NLS.bind(StrutsUIMessages.UPDATE_REFERENCE_TO_PAGE, page.getAttributeValue("path")); //$NON-NLS-2$
				}
				public void initializeValidationData(IProgressMonitor pm) {
				}
				public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
					return null;
				}
				public Change perform(IProgressMonitor pm) throws CoreException {
					return null;
				}
				public Object getModifiedElement() {
					return null;
				}
			});
		}
	}

}
