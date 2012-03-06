package org.jboss.tools.seam.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.ResourceUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetFilterSetFactory;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDataModelProvider;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetPreInstallDelegate;
import org.jboss.tools.seam.internal.core.project.facet.SeamValidatorFactory;

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

			// 1. Find template. For Seam project it is done by its runtime.
			IPath containerPath = getContainerFullPath();
			IProject currentProject = ResourcesPlugin.getWorkspace().getRoot().getFolder(containerPath).getProject();
			SeamProjectsSet set = new SeamProjectsSet(currentProject);
			IProject project = set.getWarProject();
			if(project == null) {
				return null;
			}
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if(seamProject == null) {
				return null;
			}
			SeamRuntime seamRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(seamProject.getRuntimeName());
			String seamHomePath = seamRuntime.getHomeDir();
			File dataSourceDsFile = new File(seamHomePath + "/seam-gen/resources/datasource-ds.xml"); //$NON-NLS-1$

			//2. Create filter set for Ant.
			FilterSetCollection viewFilterSetCollection = new FilterSetCollection();

			// For Seam project set is filled by reusing data model provider.
			SeamFacetInstallDataModelProvider provider = new SeamFacetInstallDataModelProvider();
			IDataModel model = (IDataModel)provider.create();
			model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());
			model.setProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, connection);
			IConnectionProfile connProfile = ProfileManager.getInstance().getProfileByName(connection.toString());
			if(connProfile == null) {
				return null;
			}
			try {
				new SeamFacetPreInstallDelegate().execute(project, null, model, new NullProgressMonitor());
			} catch (CoreException e) {
				SeamCorePlugin.getDefault().logError(e);
				return null;
			}

			FilterSet jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
			FilterSet projectFilterSet = SeamFacetFilterSetFactory.createProjectFilterSet(model);

			viewFilterSetCollection.addFilterSet(jdbcFilterSet);
			viewFilterSetCollection.addFilterSet(projectFilterSet);

			try {
				// 3. Run Ant - copy template with replaces to StringResource.				
				StringResource sr = new StringResource();

				ResourceUtils.copyResource(new FileResource(dataSourceDsFile), sr, viewFilterSetCollection,
						null, true, false, false, null, null, null, false);
				
				// 4. Return input stream for new ds file taken from temp file.
				return sr.getInputStream();
			} catch (IOException e) {
				SeamCorePlugin.getDefault().logError(e);
				return null;
			}
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
