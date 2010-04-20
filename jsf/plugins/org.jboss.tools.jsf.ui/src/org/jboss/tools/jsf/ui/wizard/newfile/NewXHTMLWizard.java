/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.wizard.newfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.html.ui.internal.wizard.NewHTMLWizard;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.wizard.newfile.NewXHTMLFileWizard;
import org.jboss.tools.common.model.ui.wizards.standard.DefaultStandardStep;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jst.web.model.handlers.CreateJSPFileSupport;

/**
 * @author mareshkau
 *
 */
public class NewXHTMLWizard extends NewHTMLWizard{
	
	private static final String HTMLWizardNewFileCreationPage = "HTMLWizardNewFileCreationPage";  //$NON-NLS-1$
	private static final String NewHTMLTemplatesWizardPage = "NewHTMLTemplatesWizardPage"; //$NON-NLS-1$
	
	private WizardNewFileCreationPage fNewFilePage;
	private NewXHTMLTemplatesWizardPage fNewFileTemplatesPage;
	private NewXHTMLFileWizard newXHTMLFileWizard;
	private NewXHTMLWizardSelectTagLibrariesPage newXHTMLWizardSelectTagLibrariesPage;  
	
	
	
	@Override
	public void addPages() {
		super.addPages();
		
		this.fNewFilePage = (WizardNewFileCreationPage) getPage(NewXHTMLWizard.HTMLWizardNewFileCreationPage);
		this.fNewFilePage.setTitle(JsfUIMessages.UI_WIZARD_XHTML_NEW_TITLE);
		this.fNewFilePage.setDescription(JsfUIMessages.UI_WIZARD_XHTML_NEW_Description);
		
		this.fNewFileTemplatesPage = new NewXHTMLTemplatesWizardPage();
		addPage(this.fNewFileTemplatesPage);
		this.newXHTMLWizardSelectTagLibrariesPage = getURISelectionPage();
		addPage(this.newXHTMLWizardSelectTagLibrariesPage);
	}
	@Override
	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		super.init(aWorkbench, aSelection);
		setWindowTitle(JsfUIMessages.UI_WIZARD_XHTML_NEW_TITLE);
		setNewXHTMLFileWizard(new NewXHTMLFileWizard());
		getNewXHTMLFileWizard().init(aWorkbench, aSelection);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public void addPage(IWizardPage page) {
		if(!NewXHTMLWizard.NewHTMLTemplatesWizardPage.equalsIgnoreCase(page.getName())){
			super.addPage(page);
		}
	}
	private NewXHTMLWizardSelectTagLibrariesPage getURISelectionPage() {
		SpecialWizardSupport support = getNewXHTMLFileWizard().getFileContext().getSupport();
		NewXHTMLWizardSelectTagLibrariesPage step = new NewXHTMLWizardSelectTagLibrariesPage(support, 1);
		try {
			support.action(SpecialWizardSupport.NEXT);
		} catch (XModelException e) {
			ModelUIPlugin.getPluginLog().logError(e);
		}
		step.setWizard(this);
		return step;
	}
	@Override
	public boolean performFinish() {
		boolean performedOK = false;
		// save user options for next use
		fNewFileTemplatesPage.saveLastSavedPreferences();

		// no file extension specified so add default extension
		String fileName = fNewFilePage.getFileName();
		if (fileName.lastIndexOf('.') == -1) {
			String newFileName = fileName+".xhtml"; //$NON-NLS-1$
			fNewFilePage.setFileName(newFileName);
		}

		// create a new empty file
		IFile file = fNewFilePage.createNewFile();

		// if there was problem with creating file, it will be null, so make
		// sure to check
		if (file != null) {
			// put template contents into file
			String templateString = fNewFileTemplatesPage.getTemplateString();
			try {
				templateString=((CreateJSPFileSupport)getNewXHTMLFileWizard().getFileContext().getSupport()).addTaglibs(templateString);
			} catch (IOException ex) {
				JsfUiPlugin.getDefault().logWarning("Problems with adding taglibs",ex); //$NON-NLS-1$
			}
			if (templateString != null) {
				templateString = applyLineDelimiter(file, templateString);
				// determine the encoding for the new file
				Preferences preference = JsfUiPlugin.getDefault().getPluginPreferences();
				String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					OutputStreamWriter outputStreamWriter = null;
					if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
						// just use default encoding
						outputStreamWriter = new OutputStreamWriter(outputStream);
					}
					else {
						outputStreamWriter = new OutputStreamWriter(outputStream, charSet);
					}
					outputStreamWriter.write(templateString);
					outputStreamWriter.flush();
					outputStreamWriter.close();
					ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
					file.setContents(inputStream, true, false, null);
					inputStream.close();
				}
				catch (Exception e) {
					JsfUiPlugin.getDefault().logWarning("Could not create contents for new HTML file", e); //$NON-NLS-1$
				}
			}

			// open the file in editor
			openEditor(file);

			// everything's fine
			performedOK = true;
		}
		return performedOK;
	}
	private String applyLineDelimiter(IFile file, String text) {
		String lineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"), new IScopeContext[] {new ProjectScope(file.getProject()), new InstanceScope() });//$NON-NLS-1$
		String convertedText = StringUtils.replace(text, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		convertedText = StringUtils.replace(convertedText, "\r", "\n");  //$NON-NLS-1$//$NON-NLS-2$
		convertedText = StringUtils.replace(convertedText, "\n", lineDelimiter); //$NON-NLS-1$
		return convertedText;
	}
	private void openEditor(final IFile file) {
		if (file != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IDE.openEditor(page, file, true);
					}
					catch (PartInitException e) {
						JsfUiPlugin.getDefault().logError(e.getMessage(), e);
					}
				}
			});
		}
	}
	/**
	 * @return the newXHTMLFileWizard
	 */
	private NewXHTMLFileWizard getNewXHTMLFileWizard() {
		return newXHTMLFileWizard;
	}
	/**
	 * @param newXHTMLFileWizard the newXHTMLFileWizard to set
	 */
	private void setNewXHTMLFileWizard(NewXHTMLFileWizard newXHTMLFileWizard) {
		this.newXHTMLFileWizard = newXHTMLFileWizard;
	}


}
