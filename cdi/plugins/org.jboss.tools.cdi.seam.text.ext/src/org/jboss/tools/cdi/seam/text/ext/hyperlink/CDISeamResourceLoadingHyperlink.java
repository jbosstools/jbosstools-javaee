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

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.util.Utils;

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
	public IFile getReadyToOpenFile(){
		IFile result = getFileFromProject(path);
		if(result != null && result.exists())
			return result;
		
		if(file == null || !file.isAccessible()) return null;
		
		path = findAndReplaceElVariable(path);
		
		IProject project = file.getProject();
		String name = Utils.trimFilePath(path);
		IPath currentPath = file.getLocation().removeLastSegments(1);
		IResource member = null;
		
		if (name.startsWith("/")) { //$NON-NLS-1$
			member = findByAbsolutePath(project, name);
		} else {
			member = findByRelativePath(project, currentPath, name);
			if (member == null && name.length() > 0) {
				member = findByAbsolutePath(project, "/" + name); //$NON-NLS-1$
			}
		}
		if (member != null && (member instanceof IFile)) {
			if (((IFile) member).exists())
				return (IFile) member;
		}
		return null;
	}
	
	private IFile findByRelativePath(IProject project, IPath basePath, String path) {
		if (path == null || path.trim().length() == 0)
			return null;
		
		path = findAndReplaceElVariable(path);
		
		IPath projectPath = project.getLocation();
		IFile member = null;

		Set<IFolder> sources = EclipseResourceUtil.getSourceFolders(file.getProject());
		for (IFolder source : sources) {
			//IPath sourcePath = source.getLocation();

			// Look in source environment
			IPath webRootPath = source.getFullPath();//projectPath.append(sourcePath);
			IPath relativePath = Utils.getRelativePath(webRootPath,	basePath);
			IPath filePath = relativePath.append(path);
			member = project.getFolder(webRootPath).getFile(filePath);
			if (member.exists()) {
				return member;
			}

		}
		return null;
	}

	private IFile findByAbsolutePath(IProject project, String path) {
		
		path = findAndReplaceElVariable(path);

		IFile member = null;

		Set<IFolder> sources = EclipseResourceUtil.getSourceFolders(file.getProject());
		for (IFolder source : sources) {
			IPath sourcePath = source.getFullPath();

			// Look in source environment
			member = project.getFolder(sourcePath).getFile(path);
			if(member.exists()) {
					return member;
			} 

		}
		return null;
	}

	@Override
	protected void doHyperlink(IRegion region) {
		IFile file = getReadyToOpenFile();
		IEditorPart part = openFileInEditor(file);
		if(part == null)
			openFileFailed();
	}
	
	@Override
	public String getHyperlinkText() {
		return NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK, path);
	}

}
