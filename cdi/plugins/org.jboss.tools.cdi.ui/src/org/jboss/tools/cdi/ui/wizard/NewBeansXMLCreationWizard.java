/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.xml.beans.model.CDIBeansConstants;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.SwtFieldEditorFactory;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewBeansXMLCreationWizard extends BasicNewResourceWizard {
	public static final String WIZARD_ID = "org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard"; //$NON-NLS-1$
	
    private WizardNewBeansXMLFileCreationPage mainPage;

	private boolean fOpenEditorOnFinish = true;

    /**
     * Creates a wizard for creating a new file resource in the workspace.
     */
    public NewBeansXMLCreationWizard() {
        super();
    }

    public void setVersion(CDIVersion version) {
    	mainPage.versionEditor.setValue(version.toString());
    }

    public String getVersion() {
    	return mainPage.versionEditor.getValueAsString();
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public void addPages() {
        super.addPages();
        mainPage = new WizardNewBeansXMLFileCreationPage("newFilePage1", getSelection());//$NON-NLS-1$
        mainPage.setTitle(CDIUIMessages.NEW_BEANS_XML_WIZARD_PAGE_NAME);
        mainPage.setDescription(CDIUIMessages.NEW_BEANS_XML_WIZARD_DESCRIPTION);
        mainPage.setImageDescriptor(CDIImages.getImageDescriptor(CDIImages.CDI_BEANS_XML_IMAGE));

        mainPage.setFileName("beans.xml");

        addPage(mainPage);
    }

    /* (non-Javadoc)
     * Method declared on IWorkbenchWizard.
     */
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        super.init(workbench, currentSelection);
        setWindowTitle(CDIUIMessages.NEW_BEANS_XML_WIZARD_TITLE);
        setNeedsProgressMonitor(true);
    }

    /* (non-Javadoc)
     * Method declared on BasicNewResourceWizard.
     */
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

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
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

		public WizardNewBeansXMLFileCreationPage(String pageName, IStructuredSelection selection) {
			super(pageName, selection);
		}
	
		public void createControl(Composite parent) {
			super.createControl(parent);
			validatePage();
		}
    	
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
					IPath current = getContainerFullPath();
					IProject p = r.getProject();
					//Prefer location of existing beans.xml to any other location.
					IPath path = getContainerWithExistingBeansXML(p);

					if(path == null) {
						//If no beans.xml exist, prefer WEB-INF if it exists
						path = ProjectHome.getWebInfPath(p);
					}
					if(current != null && current.equals(path)) {
						return;
					}
					if(path == null) {
						 Set<IFolder> fs = EclipseResourceUtil.getSourceFolders(p);
						 for (IFolder f: fs) {
							 IFolder fm = f.getFolder("META-INF");
							 if(!fm.exists()) {
								 needMetaInf = true;
								 fm = f;
							 }
							 IPath pth = fm.getFullPath();
							 if(pth.equals(current) && !needMetaInf) {
								 return;
							 }
							 if(path == null || pth.equals(current)) {
								 path = pth;
							 }
						 }
					}
					if(path != null) {
						setContainerFullPath(path);
						if(needMetaInf) {
							String value = path.append("META-INF").toString();
							try {
								setContainerValue(value);
							} catch (NoSuchFieldException e) {
								CDIUIPlugin.getDefault().logError(e);
							} catch (IllegalAccessException e) {
								CDIUIPlugin.getDefault().logError(e);
							}
						}
					}
				}
			}
		}

		void setContainerValue(String value) throws NoSuchFieldException, IllegalAccessException {
			Field f = WizardNewFileCreationPage.class.getDeclaredField("resourceGroup");
			f.setAccessible(true);
			ResourceAndContainerGroup resourceGroup = (ResourceAndContainerGroup)f.get(this);
			Field f2 = ResourceAndContainerGroup.class.getDeclaredField("containerGroup");
			f2.setAccessible(true);
			ContainerSelectionGroup containerGroup = (ContainerSelectionGroup)f2.get(resourceGroup);
			Field f3 = ContainerSelectionGroup.class.getDeclaredField("containerNameField");
			f3.setAccessible(true);
			Text text = (Text)f3.get(containerGroup);
			text.setText(value);
		}
		
		protected InputStream getInitialContents() {
			FileAnyImpl file = (FileAnyImpl)PreferenceModelUtilities.getPreferenceModel().createModelObject(getEntityName(), new Properties());
			return new ByteArrayInputStream(file.getAsText().getBytes());
		}

		String getEntityName() {
			String versionName = versionEditor.getValueAsString();
			if(versionName.equals(CDIVersion.CDI_1_0.toString())) {
				return CDIBeansConstants.ENT_CDI_BEANS;
			}

			String expectedEntityName = CDIBeansConstants.ENT_CDI_BEANS;
			for (int i = 0; i < versionName.length(); i++) {
				char c = versionName.charAt(i);
				if(Character.isDigit(c)) {
					expectedEntityName += c;
				}
			}
			if(PreferenceModelUtilities.getPreferenceModel().getMetaData().getEntity(expectedEntityName) != null) {
				return expectedEntityName;
			}

			String lastEntity = CDIBeansConstants.ENT_CDI_BEANS;
			int lastVersion = 0;
			for (String e: PreferenceModelUtilities.getPreferenceModel().getMetaData().entities()) {
				if(e.startsWith(CDIBeansConstants.ENT_CDI_BEANS) && Character.isDigit(e.charAt(e.length() - 1))) {
					String suff = e.substring(CDIBeansConstants.ENT_CDI_BEANS.length());
					int vi = Integer.parseInt(suff);
					if(vi > lastVersion) {
						lastEntity = e;
						lastVersion = vi;
					}
					
				}
			}
			return lastEntity;
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

		protected void createAdvancedControls(Composite parent) {
			Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData sd = new GridData(GridData.FILL_HORIZONTAL);
			separator.setLayoutData(sd);

			List<String> versions = new ArrayList<String>();
			for (CDIVersion v: CDIVersion.ALL_VERSIONS) {
				versions.add(v.toString());
			}
			versionEditor = SwtFieldEditorFactory.INSTANCE.createComboEditor("version", CDIUIMessages.VERSION_LABEL, versions, getInitialCDIVersion(), false, "");
			Composite c = new Composite(parent, 0);
			c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout gl = new GridLayout(3, false);
			gl.marginWidth = 5;
			gl.marginHeight = 5;
			gl.verticalSpacing = 0;
			gl.horizontalSpacing = 5;
			c.setLayout(gl);
			versionEditor.doFillIntoGrid(c);
		}

	    String getInitialCDIVersion() {
	    	IPath path = getContainerFullPath();
	    	if(path != null) {
	    		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
	    		if(p != null && CDICorePlugin.getCDI(p, false) != null) {
	    			CDIVersion v = CDICorePlugin.getCDI(p, true).getVersion();
	    			if(v != null) {
	    				return v.toString();
	    			}
	    		}
	    	}
	    	return CDIVersion.getLatestDefaultVersion().toString();
	    }

    }

	public static IPath getContainerForBeansXML(IProject p) {
		IPath path = ProjectHome.getWebInfPath(p);
		if(path == null) {
			boolean needMetaInf = false;
			 Set<IFolder> fs = EclipseResourceUtil.getSourceFolders(p);
			 for (IFolder f: fs) {
				 IFolder fm = f.getFolder("META-INF");
				 IPath pth = fm.getFullPath();
				 if(path == null) {
					 path = pth;
					 needMetaInf = !fm.exists();
				 } else if(needMetaInf && fm.exists()) {
					 path = pth;
					 needMetaInf = false;
				 }
			 }
		}
		return path;			
	}

	/**
	 * Returns path to folder that contains existing beans.xml,
	 * or null, if there is no beans.xml in WEB-INF or META-INF folders.
	 *   
	 * @param p
	 * @return
	 */
	public static IPath getContainerWithExistingBeansXML(IProject p) {
		IPath path = ProjectHome.getWebInfPath(p);
		if(path != null) {
			IFile f = p.getParent().getFile(path.append("beans.xml"));
			if(f.exists()) {
				return path;
			}
		}
		Set<IFolder> fs = EclipseResourceUtil.getSourceFolders(p);
		for (IFolder f: fs) {
			 IFolder fm = f.getFolder("META-INF");
			 if(fm.exists() && fm.getFile("beans.xml").exists()) {
				 return fm.getFullPath();
			 }
		}
		return null;			
	}

}
