/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.text.java;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.jst.web.kb.el.KbELProposalProcessor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 */
public class SeamELProposalProcessor extends KbELProposalProcessor {

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.ui.ca.ELProposalProcessor#isEnabled(org.eclipse.core.resources.IFile)
	 */
	@Override
	protected boolean isEnabled(IFile file) {
		IProject project = (file == null ? null : file.getProject());
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		return seamProject!=null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.ui.ca.ELProposalProcessor#getImage()
	 */
	@Override
	protected Image getImage() {
		return SeamCorePlugin.getDefault().getImage(SeamCorePlugin.CA_SEAM_EL_IMAGE_PATH);
	}
}