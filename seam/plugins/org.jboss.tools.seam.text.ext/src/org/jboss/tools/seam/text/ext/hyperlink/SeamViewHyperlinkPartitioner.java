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

package org.jboss.tools.seam.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPLinkHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

public class SeamViewHyperlinkPartitioner extends JSPLinkHyperlinkPartitioner {
	public static final String SEAM_VIEW_LINK_PARTITION = "org.jboss.tools.seam.text.ext.SEAM_VIEW_LINK";

	private String[] SEAM_PROJECT_NATURES = {
			SeamProject.NATURE_ID
		};

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLLinkHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return SEAM_VIEW_LINK_PARTITION;
	}

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			IFile documentFile = smw.getFile();
			if (documentFile == null)
				return false;

			IProject project = documentFile.getProject();

			for (int i = 0; i < SEAM_PROJECT_NATURES.length; i++) {
				if (project.getNature(SEAM_PROJECT_NATURES[i]) != null) 
					return true;
			}
			return false;
		} catch (CoreException x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		return recognizeNature(document) ? super.recognize(document, region) : false;
	}

}
