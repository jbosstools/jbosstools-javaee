/*************************************************************************************
 * Copyright (c) 2008-2009 JBoss by Red Hat and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.seam.ui.dialog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor.SeamRuntimeNewWizard;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard;
import org.jboss.tools.seam.ui.wizard.SeamWizardUtils;

/**
 * @author snjeza
 * 
 */
public class SeamFacetVersionChangeDialog extends TitleAreaDialog {
	private IProjectFacetVersion fv;
	private IEclipsePreferences preferences;
	private IProject warProject;
	private Image _dlgTitleImage;
	private Combo seamRuntimeCombo;
	private Text seamRuntimeNameText;
	private CheckboxTableViewer removed;
	private ListViewer added;
	private ListViewer addedEar;
	private CheckboxTableViewer removedEar;
	private Button updateLibs;
	private static IOverwriteQuery OVERWRITE_ALL = new IOverwriteQuery() {
		public String queryOverwrite(String file) {
			return ALL;
		}	
	};
	private static String[] filePattern = { "jboss-seam", "antlr-", //$NON-NLS-1$ //$NON-NLS-2$
			"commons-beanutils", "commons-digester", "commons-jci" , //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"commons-collections", //$NON-NLS-1$
			"core.", "drools-", "ajax4jsf",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"jboss-el", "jbpm", "jsf-facelets", "jxl.jar",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"mvel", "richfaces" , //$NON-NLS-1$ //$NON-NLS-2$
			"janino", "el-ri", "stringtemplate", "oscache-"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/**
	 * @param parentShell
	 * @param newWizard
	 */
	public SeamFacetVersionChangeDialog(Shell parentShell,
			IFacetedProject facetedProject, IProjectFacetVersion fv) {
		super(parentShell);
		Assert.isNotNull(facetedProject);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX
				| SWT.TITLE | SWT.BORDER | SWT.RESIZE | getDefaultOrientation());
		this.fv = fv;
		warProject = SeamWizardUtils.getRootSeamProject(facetedProject
				.getProject());
		Assert.isNotNull(warProject);
		preferences = SeamCorePlugin.getSeamPreferences(warProject);
		_dlgTitleImage = ImageDescriptor.createFromFile(SeamFormWizard.class,
				"SeamWebProjectWizBan.png").createImage(); //$NON-NLS-1$
		setTitleImage(_dlgTitleImage);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Font mainFont = parent.getFont();
		Composite area = (Composite) super.createDialogArea(parent);
		Composite contents = new Composite(area, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		// gd.heightHint = 300;
		contents.setLayoutData(gd);
		contents.setLayout(new GridLayout(3, false));
		setTitle(Messages.SeamFacetVersionChangeDialog_Seam_Runtime_Settings);
		setMessage(Messages.SeamFacetVersionChangeDialog_Set_Seam_Runtime);
		getShell().setText(Messages.SeamFacetVersionChangeDialog_Seam_Runtime_Settings);
		applyDialogFont(contents);
		initializeDialogUnits(area);

		Label oldSeamRuntimeLabel = new Label(contents, SWT.NONE);
		oldSeamRuntimeLabel.setText(Messages.SeamFacetVersionChangeDialog_Old_Seam_Runtime);
		String seamRuntimeName = ""; //$NON-NLS-1$
		if (preferences != null) {
			seamRuntimeName = preferences.get(
					ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, ""); //$NON-NLS-1$
		}
		seamRuntimeNameText = new Text(contents, SWT.BORDER);
		seamRuntimeNameText.setText(seamRuntimeName);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		seamRuntimeNameText.setEnabled(false);
		seamRuntimeNameText.setLayoutData(gd);

		Label seamRuntimeLabel = new Label(contents, SWT.NONE);
		seamRuntimeLabel.setText(Messages.SeamFacetVersionChangeDialog_New_Seam_Runtime);
		seamRuntimeCombo = new Combo(contents, SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		seamRuntimeCombo.setLayoutData(gd);
		final SeamVersion version = refreshSeamRuntimeCombo();

		Button addSeamRuntime = new Button(contents, SWT.PUSH);
		addSeamRuntime.setText(Messages.SeamFacetVersionChangeDialog_Add);
		addSeamRuntime.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<SeamRuntime> added = new ArrayList<SeamRuntime>();
				List<SeamVersion> versions = new ArrayList<SeamVersion>();
				versions.add(version);
				Wizard wiz = new SeamRuntimeNewWizard(
						(List<SeamRuntime>) new ArrayList<SeamRuntime>(Arrays
								.asList(SeamRuntimeManager.getInstance()
										.getRuntimes())), added, versions);
				WizardDialog dialog = new WizardDialog(Display.getCurrent()
						.getActiveShell(), wiz);
				int ok = dialog.open();
				if (ok == Dialog.OK && !added.isEmpty()) {
					SeamRuntimeManager.getInstance().addRuntime(added.get(0));
					refreshSeamRuntimeCombo();
				}
				refresh();
			}

		});

		Group libraryGroup = new Group(contents, SWT.NONE);
		libraryGroup.setText(Messages.SeamFacetVersionChangeDialog_Libraries);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		libraryGroup.setLayoutData(gd);
		libraryGroup.setLayout(new GridLayout(2, false));
		updateLibs = new Button(libraryGroup, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		updateLibs.setLayoutData(gd);
		updateLibs.setText(Messages.SeamFacetVersionChangeDialog_Update_libraries);
		updateLibs.setSelection(false);

		Label warProjectLabel = new Label(libraryGroup,SWT.NONE);
		warProjectLabel.setText(NLS.bind(Messages.SeamFacetVersionChangeDialog_Project, warProject.getName()));
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.CENTER);
		gd.horizontalSpan=2;
		warProjectLabel.setLayoutData(gd);
		addLabels(libraryGroup);
		
		removed = CheckboxTableViewer.newCheckList(
				libraryGroup, SWT.SINGLE | SWT.TOP | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 150;
		removed.getTable().setLayoutData(gd);
		removed.getTable().setFont(mainFont);
		IBaseLabelProvider labelProvider = new FileSetLabelProvider();
		removed.setLabelProvider(labelProvider);
		File webLibFolder = getWebLibFolder();

		IStructuredContentProvider rContentProvider = new RemovedFileSetProvider();
		removed.setContentProvider(rContentProvider);
		removed.setInput(webLibFolder);
		removed.getTable().setEnabled(false);

		removed.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				removed
						.setSelection(new StructuredSelection(event
								.getElement()));
			}
		});

		check(removed, rContentProvider);
		added = new ListViewer(libraryGroup, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 150;
		added.getList().setLayoutData(gd);
		added.getList().setFont(mainFont);
		added.setLabelProvider(labelProvider);
		SeamVersion newVersion = null;
		if (seamRuntimeCombo.getText().trim().length() > 0) {
			newVersion = SeamRuntimeManager.getInstance().findRuntimeByName(
					seamRuntimeCombo.getText()).getVersion();
		} else {
			newVersion = SeamVersion.findByString(fv.getVersionString());
		}
		IStructuredContentProvider aContentProvider = new WarFileSetProvider(
				isWarConfiguration(), newVersion);
		added.setContentProvider(aContentProvider);

		added.getList().setEnabled(false);

		File earContentsFolder = getEarContentsFolder();
		if (earContentsFolder != null && earContentsFolder.isDirectory()) {
			Label earProjectLabel = new Label(libraryGroup,SWT.NONE);
			String earProjectName = preferences.get(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, ""); //$NON-NLS-1$
			earProjectLabel.setText(NLS.bind(Messages.SeamFacetVersionChangeDialog_Project, earProjectName));
			gd = new GridData(GridData.FILL_HORIZONTAL | GridData.CENTER);
			gd.horizontalSpan=2;
			earProjectLabel.setLayoutData(gd);
			addLabels(libraryGroup);
			removedEar = CheckboxTableViewer
					.newCheckList(libraryGroup, SWT.SINGLE | SWT.TOP
							| SWT.BORDER);
			gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 150;
			removedEar.getTable().setLayoutData(gd);
			removedEar.getTable().setFont(mainFont);
			removedEar.setLabelProvider(labelProvider);
			
			IStructuredContentProvider rEarContentProvider = new RemovedFileSetProvider();
			removedEar.setContentProvider(rEarContentProvider);
			removedEar.setInput(earContentsFolder);
			removedEar.getTable().setEnabled(false);

			removedEar.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					removedEar.setSelection(new StructuredSelection(event
							.getElement()));
				}
			});

			check(removedEar, rEarContentProvider);
			addedEar = new ListViewer(libraryGroup,
					SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 150;
			addedEar.getList().setLayoutData(gd);
			addedEar.getList().setFont(mainFont);
			addedEar.setLabelProvider(labelProvider);
			IStructuredContentProvider aEarContentProvider = new EarFileSetProvider(
					newVersion);
			addedEar.setContentProvider(aEarContentProvider);

			addedEar.getList().setEnabled(false);
			
		}
		updateLibs.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				added.getList().setEnabled(updateLibs.getSelection());
				removed.getTable().setEnabled(updateLibs.getSelection());
				if (addedEar != null) {
					removedEar.getTable().setEnabled(updateLibs.getSelection());
					addedEar.getList().setEnabled(updateLibs.getSelection());
				}
			}

		});
		Group noteGroup = new Group(contents, SWT.NONE);
		noteGroup.setText(Messages.SeamFacetVersionChangeDialog_Note);
		noteGroup.setLayout(new GridLayout(2,false));
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan=3;
		noteGroup.setLayoutData(gd);
		
		Label noteImage = new Label(noteGroup,SWT.NONE);
		Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		noteImage.setImage(image);
		Label noteLabel = new Label(noteGroup,SWT.NONE);
		noteLabel.setText(Messages.SeamFacetVersionChangeDialog_Note_description);
		
		seamRuntimeCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
			
		});
		refresh();
		return area;
	}

	private SeamVersion refreshSeamRuntimeCombo() {
		final SeamVersion version = SeamVersion.findByString(fv
				.getVersionString());
		String[] runtimeNames = getRuntimeNames(version);
		seamRuntimeCombo.setItems(runtimeNames);
		if (runtimeNames.length > 0 && seamRuntimeCombo.getText().trim().length() <=0 ) {
			seamRuntimeCombo.select(0);
		}
		
		return version;
	}

	private void refresh() {
		updateButton();
		File seamHomePath = getSeamHomePath();
		added.setInput(seamHomePath);
		if (addedEar != null) {
			addedEar.setInput(seamHomePath);
		}
	}

	private void updateButton() {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			boolean ok = seamRuntimeCombo.getText().trim().length() > 0;
			okButton.setEnabled(ok);
		}
	}

	private void addLabels(Composite composite) {
		Label removedLibs = new Label(composite, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		removedLibs.setLayoutData(gd);
		removedLibs.setText(Messages.SeamFacetVersionChangeDialog_Libraries_to_be_removed);
		
		Label addedLibs = new Label(composite, SWT.NONE);
		addedLibs.setText(Messages.SeamFacetVersionChangeDialog_Libraries_to_be_added);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addedLibs.setLayoutData(gd);
	}
	private File getEarContentsFolder() {
		IContainer earContentsFolder = getEarContentsEclipseFolder();
		if (earContentsFolder == null) {
			return null;
		} else {
			return earContentsFolder.getLocation().toFile();
		}
	}

	private IContainer getEarContentsEclipseFolder() {
		String earProjectName = preferences.get(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, ""); //$NON-NLS-1$
		if (earProjectName != null && earProjectName.trim().length() > 0) {
			IProject earProject = ResourcesPlugin.getWorkspace().getRoot().getProject(earProjectName);
			if (earProject != null && earProject.isOpen()) {
				IVirtualComponent component = ComponentCore.createComponent(earProject);
				IVirtualFolder rootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
				return rootVirtFolder.getUnderlyingFolder();
			}
		}
		return null;
		
	}
	
	private void check(final CheckboxTableViewer removed,
			IStructuredContentProvider rContentProvider) {
		Object[] elements = rContentProvider.getElements(null);
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof File) {
				File file = (File) elements[i];
				removed.setChecked(elements[i], getChecked(file));

			}
		}
	}

	private boolean getChecked(File file) {
		for (int i = 0; i < filePattern.length; i++) {
			if (file.getName().startsWith(filePattern[i])) {
				return true;
			}
		}
		return false;
	}

	private File getSeamHomePath() {
		String selectedSeamName = seamRuntimeCombo.getText();
		if (selectedSeamName != null && selectedSeamName.trim().length() > 0) {
			SeamRuntime selectedSeamRuntime = SeamRuntimeManager.getInstance()
					.findRuntimeByName(selectedSeamName);
			if (selectedSeamRuntime != null) {
				return new File(selectedSeamRuntime.getHomeDir());
			}
		}
		return null;
	}

	private File getWebLibFolder() {
		IContainer webLibFolder = getWebLibEclipseFolder();

		return webLibFolder.getLocation().toFile();

	}

	private IContainer getWebLibEclipseFolder() {
		IVirtualComponent component = ComponentCore
				.createComponent(warProject);
		IVirtualFolder libFolder = component.getRootFolder().getFolder(
				new Path("/WEB-INF/lib")); //$NON-NLS-1$
		IContainer webLibFolder = libFolder.getUnderlyingFolder();
		return webLibFolder;
	}

	private String[] getRuntimeNames(SeamVersion seamVersion) {
		SeamRuntime[] rts = SeamRuntimeManager.getInstance().getRuntimes(
				seamVersion);
		String[] names = new String[rts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = rts[i].getName();
		}
		return names;
	}

	@Override
	public boolean close() {
		if (_dlgTitleImage != null) {
			_dlgTitleImage.dispose();
		}
		return super.close();
	}

	private boolean isWarConfiguration() {
		if (preferences == null) {
			return false;
		}
		return preferences.get(
				ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
				ISeamFacetDataModelProperties.DEPLOY_AS_WAR).equals(
				ISeamFacetDataModelProperties.DEPLOY_AS_WAR);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		updateButton();
	}

	@Override
	protected void okPressed() {
		
		try {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					setSeamSettings();
					updateLibraries();
				}
				
			}, new NullProgressMonitor());
			
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		super.okPressed();
	}

	protected void setSeamSettings() {
		IEclipsePreferences prefs = preferences;
		if(prefs==null) {
			IScopeContext projectScope = new ProjectScope(warProject);
			prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
		}
		prefs.put(ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION, 
				ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION_1_1);
		prefs.put(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, 
				seamRuntimeCombo.getText());
	
	}

	private void updateLibraries() throws CoreException {
		if (updateLibs.getSelection()) {
			IContainer webLibContainer = getWebLibEclipseFolder();
			processProject(webLibContainer,removed,added);
			if (removedEar != null) {
				IContainer earContentsFolder = getEarContentsEclipseFolder();
				processProject(earContentsFolder, removedEar, addedEar);
			}
		}
	}

	private void processProject(IContainer container,
			CheckboxTableViewer cbtViewer, ListViewer listViewer)
			throws CoreException {
		if (container != null) {
			removeFiles(container, cbtViewer.getCheckedElements());
			IStructuredContentProvider structuredProvider = (IStructuredContentProvider) listViewer
					.getContentProvider();
			Object[] addedElements = structuredProvider.getElements(null);
			Map<File, List<File>> parentMaps = getImportMaps(addedElements);
			IPath containerPath = container.getFullPath();
			IImportStructureProvider provider = FileSystemStructureProvider.INSTANCE;
			for (File parent : parentMaps.keySet()) {
				try {
					ImportOperation op = new ImportOperation(containerPath,
							parent, provider, OVERWRITE_ALL, parentMaps
									.get(parent));
					op.setCreateContainerStructure(false);
					op.run(new NullProgressMonitor());
				} catch (InvocationTargetException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				} catch (InterruptedException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
			}
		}
	}

	private void removeFiles(IContainer container, Object[] elements) throws CoreException {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof File) {
				File file = (File) elements[i];
				String name = file.getName();
				IResource resource = container.findMember(name);
				resource.delete(true, null);
			}
		}
	}

	private Map<File, List<File>> getImportMaps(Object[] elements) {
		Map<File, List<File>> parentMaps = new HashMap<File, List<File>>();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof File) {
				File file = (File) elements[i];
				File parent = file.getParentFile();
				Set<File> parents = parentMaps.keySet();
				if (parents.contains(parent)) {
					List<File> files = parentMaps.get(parent);
					files.add(file);
				} else {
					List<File> files = new ArrayList<File>();
					files.add(file);
					parentMaps.put(parent, files);
				}
			}
		}
		return parentMaps;
	}
}
