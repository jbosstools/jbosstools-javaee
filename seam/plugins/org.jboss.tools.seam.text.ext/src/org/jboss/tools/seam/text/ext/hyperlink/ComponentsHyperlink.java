/*******************************************************************************
 * Copyright (c) 2009-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.text.ext.hyperlink;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

public class ComponentsHyperlink extends AbstractHyperlink {
	private String hyperlinkText = "";

	@Override
	protected void doHyperlink(IRegion region) {
		if (region == null)
			return;

		try {
			IDocument document = getDocument();
			hyperlinkText = document
					.get(region.getOffset(), region.getLength());
		} catch (BadLocationException ex) {
			SeamExtPlugin.getPluginLog().logError(ex);
		}

		IHyperlinkRegion partition = (getHyperlinkRegion() instanceof IHyperlinkRegion) ?
				(IHyperlinkRegion)getHyperlinkRegion() : null;
		
		String partitionType = partition == null ? null : partition.getType();
		
		if (partitionType == ComponentsHyperlinkPartitioner.BPM_DEFINITION_PARTITION)
			doBpmDefinitionHyperlink(region);
		else if (partitionType == ComponentsHyperlinkPartitioner.DROOLS_RULE_PARTITION)
			doDroolsRuleHyperlink(region);
	}

	private void doDroolsRuleHyperlink(IRegion region) {
		IFile file = findDroolsRuleFile();
		
		IEditorPart part = null;
		if (file != null)
			part = openFileInEditor(file);
		
		if (part == null)
			openFileFailed();
	}
	
	private IFile findDroolsRuleFile(){
		IFile file;
		
		IProject project = getProject();
		IResource[] sources = EclipseResourceUtil.getJavaSourceRoots(project);

		for (IResource resource : sources) {
			String path = resource.getFullPath().removeFirstSegments(1)
					+ hyperlinkText;
			file = project.getFile(path);
			if(file != null && file.exists())
				return file;
		}
		
		return findDefinitionFile();
	}

	private void doBpmDefinitionHyperlink(IRegion region) {
		IFile file = findDefinitionFile();
		IEditorPart part = null;
		if (file != null)
			part = openFileInEditor(file);
		
		if (part == null)
			openFileFailed();
	}
	
	private IFile findDefinitionFile(){
		IFile file;
		SeamProjectsSet projectsSet = SeamProjectsSet.create(getProject());
		
		IContainer webContent = projectsSet.getDefaultViewsFolder();
		
		if(webContent != null){
			file = webContent.getFile(new Path(hyperlinkText));
			if(file != null && file.exists())
				return file;
		}
		
		IContainer earContent = projectsSet.getDefaultEarViewsFolder();
		
		if(earContent != null){
			file = earContent.getFile(new Path(hyperlinkText));
			if(file != null && file.exists())
				return file;
		}
		
		IContainer ejbSource = projectsSet.getDefaultEjbSourceFolder();
		
		if(ejbSource != null){
			file = ejbSource.getFile(new Path(hyperlinkText));
			if(file != null && file.exists())
				return file;
		}
		
		return null;
	}

	private IProject getProject() {
		IFile documentFile = getFile();
		if (documentFile == null || !documentFile.isAccessible())
			return null;

		IProject project = documentFile.getProject();

		return project;
	}

	@Override
	public String getHyperlinkText() {
		return hyperlinkText;
	}
}
