package org.jboss.tools.seam.ui.wizard;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.ui.internal.project.facet.DataModelSynchronizer;
import org.jboss.tools.seam.ui.internal.project.facet.SeamValidatorFactory;

public class SeamDSXMLWizard extends BasicNewResourceWizard {
	public static final String WIZARD_ID = "org.jboss.tools.seam.ui.wizard.SeamDSXMLWizard"; //$NON-NLS-1$

	WizardNewDSXMLFileCreationPage mainPage;

	private boolean fOpenEditorOnFinish;

	public SeamDSXMLWizard() {
		
	}

    public void addPages() {
        super.addPages();
        mainPage = new WizardNewDSXMLFileCreationPage("newFilePage1", getSelection());
        mainPage.setTitle("New DS XML");
        
        addPage(mainPage);
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

	class WizardNewDSXMLFileCreationPage extends WizardNewFileCreationPage {
		private IFieldEditor connProfileSelEditor;

		public WizardNewDSXMLFileCreationPage(String pageName, IStructuredSelection selection) {
			super(pageName, selection);
		}
	
		protected InputStream getInitialContents() {
			Object connection = connProfileSelEditor.getValue();
			//TODO generate text here

			return null;
		}

		public void createControl(Composite parent) {
			super.createControl(parent);

			Composite topLevel = (Composite)getControl();
			connProfileSelEditor = SeamWizardFactory.createConnectionProfileSelectionFieldEditor(getConnectionProfileDefaultValue(), new IValidator() {
				public Map<String, IStatus> validate(Object value, Object context) {
					validatePage();
					return SeamValidatorFactory.NO_ERRORS;
				}
			});
			Composite q = new Composite(topLevel, 0);
			GridLayout l = new GridLayout(4, false);
			q.setLayout(l);
			connProfileSelEditor.doFillIntoGrid(q);
//			sync.register(connProfileSelEditor);
			
			validatePage();
		}
    	
	}

	/**
	 * @return
	 */
	private Object getConnectionProfileDefaultValue() {
		String defaultDs = SeamProjectPreferences
				.getStringPreference(SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE);
		return getConnectionProfileNameList().contains(defaultDs) ? defaultDs
				: ""; //$NON-NLS-1$
	}

	private static List<String> getConnectionProfileNameList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfilesByCategory(
						"org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		List<String> names = new ArrayList<String>();
		for (IConnectionProfile connectionProfile : profiles) {
			names.add(connectionProfile.getName());
		}
		return names;
	}

}
