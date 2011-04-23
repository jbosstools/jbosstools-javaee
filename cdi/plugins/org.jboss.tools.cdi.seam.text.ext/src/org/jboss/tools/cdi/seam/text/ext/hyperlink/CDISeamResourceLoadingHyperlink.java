/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class CDISeamResourceLoadingHyperlink extends AbstractHyperlink{
	private IRegion region;
	private String path;
	private IFile file;
	
	public CDISeamResourceLoadingHyperlink(IFile file, IDocument document, IRegion region, String path){
		super();
		this.file = file;
		this.region = region;
		this.path = path;
		setDocument(document);
	}
	
	protected IFile getFile(){
		return file;
	}

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	@Override
	protected void doHyperlink(IRegion region) {
		IFile file = getFile().getProject().getFile(path);
		IEditorPart part = openFileInEditor(file);
		if(part == null)
			openFileFailed();
	}
	
	@Override
	public String getHyperlinkText() {
		return NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK, path);
	}

}
