/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.batch.ui.internal.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.misc.ContainerSelectionGroup;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchUtil;
import org.jboss.tools.batch.internal.core.impl.definition.BatchJobDefinition;
import org.jboss.tools.batch.ui.JobImages;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.SwtFieldEditorFactory;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewJobXMLCreationWizard extends BasicNewResourceWizard {
	public static final String WIZARD_ID = "org.jboss.tools.batch.ui.internal.wizard.NewJobXMLCreationWizard"; //$NON-NLS-1$
	public static final String PAGE_NAME = "newJobFilePage"; //$NON-NLS-1$

	private WizardNewBeansXMLFileCreationPage mainPage;

	private boolean fOpenEditorOnFinish = true;

	/**
	 * Creates a wizard for creating a new file resource in the workspace.
	 */
	public NewJobXMLCreationWizard() {
		super();
	}

	public void setVersion(String version) {
		if(mainPage.versionEditor != null)  {
			mainPage.versionEditor.setValue(version.toString());
		}
	}

	public void setID(String id) {
		if(mainPage.idEditor != null)  {
			mainPage.idEditor.setValue(id);
		}
	}

	public String getVersion() {
		return mainPage.versionEditor != null ? mainPage.versionEditor.getValueAsString() : mainPage.getInitialJobVersion();
	}

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new WizardNewBeansXMLFileCreationPage(PAGE_NAME, getSelection());
		mainPage.setTitle(WizardMessages.NEW_JOB_XML_WIZARD_TITLE);
		mainPage.setDescription(WizardMessages.NEW_JOB_XML_WIZARD_DESCRIPTION);
		mainPage.setImageDescriptor(JobImages.getImageDescriptor(JobImages.NEW_JOB_XML_IMAGE));

		mainPage.setFileName("job.xml");

		addPage(mainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle(WizardMessages.NEW_JOB_XML_WIZARD_TITLE);
		setNeedsProgressMonitor(true);
	}

	@Override
	protected void initializeDefaultPageImageDescriptor() {
		ImageDescriptor desc = IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/newfile_wiz.png");//$NON-NLS-1$
		setDefaultPageImageDescriptor(desc);
	}

	public boolean isOpenEditorAfterFinish() {
		return fOpenEditorOnFinish;
	}

	public void setOpenEditorAfterFinish(boolean set) {
		this.fOpenEditorOnFinish = set;
	}

	@Override
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		if (file == null) {
			return false;
		}

		selectAndReveal(file);
		if (fOpenEditorOnFinish) {
			// Open editor on new file.
			IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
			try {
				if (dw != null) {
					IWorkbenchPage page = dw.getActivePage();
					if (page != null) {
						IDE.openEditor(page, file, true);
					}
				}
			} catch (PartInitException e) {
				DialogUtil.openError(dw.getShell(), ResourceMessages.FileResource_errorMessage, e.getMessage(), e);
			}
		}
		return true;
	}

	class WizardNewBeansXMLFileCreationPage extends WizardNewFileCreationPage {
		IFieldEditor versionEditor = null;
		IFieldEditor idEditor = null;

		public WizardNewBeansXMLFileCreationPage(String pageName, IStructuredSelection selection) {
			super(pageName, selection);
		}

		@Override
		public void createControl(Composite parent) {
			super.createControl(parent);
			validatePage();
		}

		@Override
		protected void initialPopulateContainerNameField() {
			super.initialPopulateContainerNameField();
			if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
				Object o = ((IStructuredSelection)selection).getFirstElement();
				IResource r = null;
				if(o instanceof IResource) {
					r = (IResource)o;
				} else if(o instanceof IAdaptable) {
					r = (IResource)((IAdaptable)o).getAdapter(IResource.class);
				}
				if(r != null) {
					boolean needMetaInf = false;
					boolean needBatchJobs = false;
					IPath current = getContainerFullPath();
					IProject p = r.getProject();
					//Prefer location of existing beans.xml to any other location.
					IPath path = getBatchJobsContainer(p);

					if(current != null && current.equals(path)) {
						return;
					}
					if(path == null) {
						for (IResource f: EclipseUtil.getJavaSourceRoots(p)) {
							if(f instanceof IFolder) {
								IFolder fm = ((IFolder)f).getFolder(BatchConstants.META_INF);
								IFolder bj = fm.getFolder(BatchConstants.BATCH_JOBS);
								if(!bj.exists()) {
									needBatchJobs = true;
									if(fm.exists()) {
										bj = fm;
									} else {
										needMetaInf = true;
										bj = ((IFolder)f);
									}
								}
								IPath pth = bj.getFullPath();
								if(pth.equals(current) && !needMetaInf) {
									return;
								}
								if(path == null || pth.equals(current)) {
									path = pth;
								}
							}
						}
					}
					if(path != null) {
						setContainerFullPath(path);
						if(needMetaInf) {
							String value = needBatchJobs ? path.append(BatchConstants.META_INF).append(BatchConstants.BATCH_JOBS).toString()
								: path.append(BatchConstants.META_INF).toString();
							try {
								setContainerValue(value);
							} catch (NoSuchFieldException e) {
								BatchCorePlugin.pluginLog().logError(e);
							} catch (IllegalAccessException e) {
								BatchCorePlugin.pluginLog().logError(e);
							}
						}
					}
				}
			}
		}

		void setContainerValue(String value) throws NoSuchFieldException, IllegalAccessException {
			Field f = WizardNewFileCreationPage.class.getDeclaredField("resourceGroup"); //$NON-NLS-1$
			f.setAccessible(true);
			ResourceAndContainerGroup resourceGroup = (ResourceAndContainerGroup)f.get(this);
			Field f2 = ResourceAndContainerGroup.class.getDeclaredField("containerGroup"); //$NON-NLS-1$
			f2.setAccessible(true);
			ContainerSelectionGroup containerGroup = (ContainerSelectionGroup)f2.get(resourceGroup);
			Field f3 = ContainerSelectionGroup.class.getDeclaredField("containerNameField"); //$NON-NLS-1$
			f3.setAccessible(true);
			Text text = (Text)f3.get(containerGroup);
			text.setText(value);
		}

		String JOB_ID_VAR = "%job-id%"; //$NON-NLS-1$
		String DEFAULT_FILE_NAME = "job.xml"; //$NON-NLS-1$
		String DEFAULT_JOB_ID = "job"; //$NON-NLS-1$

		@Override
		protected InputStream getInitialContents() {
			File f = null;
			try {
				f = new File(BatchUtil.getTemplatesFolder(), DEFAULT_FILE_NAME);
			} catch (IOException e) {
				BatchCorePlugin.pluginLog().logError(e);
				return new ByteArrayInputStream(new byte[0]);
			}
			String text = FileUtil.readFile(f);
			String id = mainPage.idEditor.getValueAsString().trim();
			int i = text.indexOf(JOB_ID_VAR);
			if(i > 0) {
				text = text.substring(0, i) + id + text.substring(i + JOB_ID_VAR.length());
			}
			return new ByteArrayInputStream(text.getBytes());
		}

		//Advanced capability is removed.
		@Override
		protected IStatus validateLinkedResource() {
			return Status.OK_STATUS;
		}

		//Advanced capability is removed.
		@Override
		protected void createLinkTarget() {
		}

		@Override
		protected void createAdvancedControls(Composite parent) {
			Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData sd = new GridData(GridData.FILL_HORIZONTAL);
			separator.setLayoutData(sd);

			List<String> versions = getVersions();
			if(versions.size() > 1) {
				//While there is the only Batch version 1.0, this field editor is not created. 
				versionEditor = SwtFieldEditorFactory.INSTANCE.createComboEditor(BatchConstants.ATTR_VERSION, WizardMessages.versionLabel, versions, getInitialJobVersion(), false, "");
				versionEditor.doFillIntoGrid(createComposite(parent));
			}

			idEditor = SwtFieldEditorFactory.INSTANCE.createTextEditor(BatchConstants.ATTR_ID, WizardMessages.idLabel, DEFAULT_JOB_ID);
			idEditor.doFillIntoGrid(createComposite(parent));
			idEditor.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if(!isSettingDefaultJobId) {
						isJobIdDefault = false;
						setPageComplete(validatePage());
					}
				}
			});
		}

		private Composite createComposite(Composite parent) {
			Composite c = new Composite(parent, 0);
			c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout gl = new GridLayout(3, false);
			gl.marginWidth = 5;
			gl.marginHeight = 5;
			gl.verticalSpacing = 0;
			gl.horizontalSpacing = 5;
			c.setLayout(gl);
			return c;
		}

		String getInitialJobVersion() {
			return "1.0";
		}

		List<String> getVersions() {
			List<String> versions = new ArrayList<String>();
			versions.add(getInitialJobVersion());
			//Update when new Batch versions appear.
			return versions;
		}

		Set<String> ids = new HashSet<String>();
		IPath lastPath = null;
		boolean isJobIdDefault = true;
		boolean isSettingDefaultJobId = false;

		@Override
		protected boolean validatePage() {
			boolean valid = super.validatePage();
			if(idEditor == null) {
				return valid;
			}
			if(isJobIdDefault) {
				String fn = getFileName();
				if(fn.endsWith(".xml")) {
					fn = fn.substring(0, fn.length() - 4).trim();
				}
				String id = idEditor.getValueAsString().trim();
				if(!id.equals(fn)) {
					isSettingDefaultJobId = true;
					idEditor.setValue(fn);
					isSettingDefaultJobId = false;
				}
			}
			if(valid) {
				String id = idEditor.getValueAsString().trim();
				if(id.length() == 0) {
					setErrorMessage(WizardMessages.errorIdIsRequired);
					return false;
				}
				IPath p = getContainerFullPath();
				if(!p.equals(lastPath)) {
					IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(p);
					if(folder.exists()) {
						BatchProject bp = (BatchProject)BatchCorePlugin.getBatchProject(folder.getProject(), true);
						if(bp != null) {
							ids = new HashSet<String>();
							for (BatchJobDefinition def: bp.getDeclaredBatchJobDefinitions()) {
								ids.add(def.getJobID());
							}
						}
					}
				}
				if(ids.contains(id)) {
					setMessage(WizardMessages.errorJobIdIsNotUnique, DialogPage.WARNING);
				}
			}
			return valid;
		}
	}

	/**
	 * Returns path to existing batch-jobs folder that,
	 * or null, if there is no batch-jobs folder in META-INF folders.
	 *   
	 * @param p
	 * @return
	 */
	public static IPath getBatchJobsContainer(IProject p) {
		for (IResource f: EclipseUtil.getJavaSourceRoots(p)) {
			if(f instanceof IFolder) {
				IFolder fm = ((IFolder)f).getFolder(BatchConstants.META_INF);
				IFolder bj = fm.getFolder(BatchConstants.BATCH_JOBS);
				if(bj.exists()) {
					return bj.getFullPath();
				}
			}
		}
		return null;			
	}

}
