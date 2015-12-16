/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.internal.wizard;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchUtil;
import org.jboss.tools.batch.internal.core.impl.PreferredPackageManager;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewBatchArtifactWizard extends NewElementWizard implements BatchConstants {
	public static final String WIZARD_ID = "org.jboss.tools.batch.ui.internal.wizard.NewBatchArtifactWizard";
	protected boolean fOpenEditorOnFinish = true;

	protected NewBatchArtifactWizardPage fPage;

	protected List<BatchArtifactType> types = null;

	public NewBatchArtifactWizard() {
		setWindowTitle(WizardMessages.NEW_BATCH_ARTIFACT_WIZARD_TITLE);
	}

	public void setTypes(List<BatchArtifactType> types) {
		this.types = types;
	}

	public List<BatchArtifactType> getTypes() {
		return types;
	}

	@Override
	public void addPages() {
		if (fPage == null) {
			fPage = new NewBatchArtifactWizardPage();
			fPage.setWizard(this);
			((NewClassWizardPage)fPage).init(getSelection());
		} else {
			fPage.setWizard(this);
		}
		addPage(fPage);
	}

	public NewBatchArtifactWizardPage getPage() {
		return fPage;
	}
 
	public boolean isOpenEditorAfterFinish() {
		return fOpenEditorOnFinish;
	}

	public void setOpenEditorAfterFinish(boolean set) {
		this.fOpenEditorOnFinish = set;
	}

	public String getQualifiedClassName() {
		IType type = fPage.getCreatedType();
		return type == null ? "" : type.getFullyQualifiedName();
	}

	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}

	@Override
	public IJavaElement getCreatedElement() {
		return fPage.getCreatedType();
	}

	@Override
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res= super.performFinish();
		if (res) {
			registerInBatchXML();
			IResource resource= fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (fOpenEditorOnFinish) {
					openResource((IFile) resource);
				}
				String packName = fPage.getPackageFragment().getPath().toString();
				BatchArtifactType type = fPage.getArtifactType();
				PreferredPackageManager.savePreferredPackage(resource.getProject(), type, packName);
			}
		}
		return res;
	}

	private void registerInBatchXML() {
		if(BatchFieldEditorFactory.LOADER_OPTION_XML.equals(fPage.nameOptions.getValueAsString())) {
			IPath path = BatchUtil.getBatchXMLPath(fPage.getJavaProject().getProject());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path); 

			String insert = "\t<" + ATTR_REF + " " + ATTR_CLASS + "=\"" + fPage.getQualifiedName() + "\" "
					+ ATTR_ID + "=\"" + fPage.name.getValueAsString() + "\"/>" + "\n";

			try {
				IContainer c = file.getParent();
				while(!c.exists()) c = c.getParent();
				File f = null;
				if(!file.exists()) {				
					f = new File(BatchUtil.getTemplatesFolder(), BATCH_XML);
				} else {
					f = file.getLocation().toFile();
				}
				String text = FileUtil.readFile(f);
				int i = text.indexOf("</batch-artifacts>");
				text = text.substring(0, i) + insert + text.substring(i);
				FileUtil.writeFile(file.getLocation().toFile(), text);
				c.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (IOException e) {
				BatchCorePlugin.pluginLog().logError(e);
			} catch (CoreException e) {
				BatchCorePlugin.pluginLog().logError(e);
			}
		}
	}

}
