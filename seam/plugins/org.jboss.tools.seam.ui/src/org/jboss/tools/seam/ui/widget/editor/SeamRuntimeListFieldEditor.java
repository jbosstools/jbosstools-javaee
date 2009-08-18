/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.widget.editor;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard;

/**
 * Control that have table and buttons for editing Seam Runtime list
 * 
 * @author eskimo
 * 
 */
public class SeamRuntimeListFieldEditor extends BaseFieldEditor {
	
	// ------------------------------------------------------------------------
	// Layout parameters
	// ------------------------------------------------------------------------
	
	static final int GL_COLUMNS = 2;
	static final int GL_HINT_HEIGHT = 200;
	static final int TC_DEFAULT_WIDTH = 21;
	static final int TC_NAME_WIDTH = 100;
	static final int TC_VERSION_WIDTH = 50;
	static final int TC_PATH_WIDTH = 100;

	// ------------------------------------------------------------------------
	// Field declarations
	// ------------------------------------------------------------------------
 
	private CheckboxTableViewer tableView = null;

	private Composite root = null;

	private ActionPanel actionPanel;
	
	private Map<SeamRuntime, SeamRuntime> changed = new HashMap<SeamRuntime, SeamRuntime>();

	private List<SeamRuntime> checkedElements = new ArrayList<SeamRuntime>();

	private List<SeamRuntime> added = new ArrayList<SeamRuntime>();

	private List<SeamRuntime> removed = new ArrayList<SeamRuntime>();

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	
	/**
	 * Control for editing SeamRuntime list
	 * 
	 * @param name
	 *            String
	 * @param label
	 *            String
	 * @param defaultValue
	 *            Object
	 */
	public SeamRuntimeListFieldEditor(String name, String label,
			Object defaultValue) {
		super(name, label, defaultValue);
	}

	/**
	 * TBD
	 * 
	 * @return List&lt;SeamRuntime&gt;
	 */
	public List<SeamRuntime> getDefaultSeamRuntimes() {
		return checkedElements;
	}

	/**
	 * TBD
	 * 
	 * @return List&lt;SeamRuntime&gt;
	 */
	public List<SeamRuntime> getAddedSeamRuntimes() {
		return added;
	}

	/**
	 * TBD
	 * 
	 * @return List&lt;SeamRuntime&gt;
	 */
	public Map<SeamRuntime, SeamRuntime> getChangedSeamRuntimes() {
		return changed;
	}

	/**
	 * TBD
	 * 
	 * @return List&lt;SeamRuntime&gt;
	 */
	public List<SeamRuntime> getRemoved() {
		return removed;
	}

	/**
	 * TBD
	 * 
	 * @param composite
	 *            Object - instance of Composite
	 * @return Object[]
	 */
	@Override
	public Object[] getEditorControls(Object composite) {

		root = new Composite((Composite) composite, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		root.setLayoutData(gd);

		root.setLayout(new FormLayout());
		createTableView();
		createActionBar();
		
		FormData tableData = new FormData();
		tableData.left = new FormAttachment(0,5);
		tableData.right = new FormAttachment(actionPanel, -5);
		tableData.top = new FormAttachment(0,5);
		tableData.bottom = new FormAttachment(100,-5);
		tableView.getControl().setLayoutData(tableData);
		
		FormData actionsData = new FormData();
		actionsData.top = new FormAttachment(0,5);
		actionsData.bottom = new FormAttachment(100,-5);
		actionsData.right = new FormAttachment(100,-5);
		actionPanel.setLayoutData(actionsData);
		return new Control[] {root};
	}
	
	protected void createTableView() {
		tableView = CheckboxTableViewer.newCheckList(root, SWT.V_SCROLL
				| SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);

		TableColumn tc1 = new TableColumn(tableView.getTable(), SWT.CENTER);
		tc1.setWidth(TC_DEFAULT_WIDTH);
		tc1.setResizable(false);

		TableColumn tc2 = new TableColumn(tableView.getTable(), SWT.LEFT);
		tc2.setWidth(TC_NAME_WIDTH);
		tc2.setText(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME);

		TableColumn tc3 = new TableColumn(tableView.getTable(), SWT.LEFT);
		tc3.setWidth(TC_VERSION_WIDTH);
		tc3.setText(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION);

		TableColumn tc4 = new TableColumn(tableView.getTable(), SWT.LEFT);
		tc4.setWidth(TC_PATH_WIDTH);
		tc4.setText(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_PATH);

		tableView.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof List) {
					return ((List<SeamRuntime>) inputElement).toArray();
				} else {
					throw new IllegalArgumentException(
							SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_INPUTELEMENT_MUST_BE_LIST);
				}
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				viewer.refresh();
			}
		});

		tableView.setLabelProvider(new ITableLabelProvider() {

			private static final int TC_DEFAULT_NUMBER = 0;
			private static final int TC_NAME_NUMBER = 1;
			private static final int TC_VERSION_NUMBER = 2;
			private static final int TC_PATH_NUMBER = 3;

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				SeamRuntime rt = (SeamRuntime) element;
				if (columnIndex == TC_DEFAULT_NUMBER) {
					return ""; //$NON-NLS-1$
				}
				if (columnIndex == TC_NAME_NUMBER) {
					return rt.getName();
				}
				if (columnIndex == TC_VERSION_NUMBER) {
					return rt.getVersion().toString();
				}
				if (columnIndex == TC_PATH_NUMBER) {
					return rt.getHomeDir();
				}
				return ""; //$NON-NLS-1$
			}
		});

		tableView.setInput(getValue());
		tableView.getTable().setLinesVisible(true);
		tableView.getTable().setHeaderVisible(true);
		tableView.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				SeamRuntime selRt = (SeamRuntime) event.getElement();
				if (event.getChecked()) {
					SeamRuntime deselRt = null;
					Object[] selRts = tableView.getCheckedElements();

					for (int i = 0; i < selRts.length; i++) {
						SeamRuntime rt = (SeamRuntime) selRts[i];
						if (rt.getVersion() == selRt.getVersion()
								&& rt != selRt) {
							deselRt = rt;
							break;
						}
					}

					if (deselRt != null) {
						Object[] newChecked = new Object[selRts.length - 1];
						checkedElements.clear();
						int i = 0;
						for (Object object : selRts) {
							SeamRuntime rt = (SeamRuntime) object;
							if (rt.getVersion() != selRt.getVersion()
									|| rt == selRt) {
								newChecked[i] = rt;
								checkedElements.add(rt);
								i++;
							}
						}
						tableView.setCheckedElements(newChecked);
					} else {
						checkedElements.add((SeamRuntime)event.getElement());
					}
				} else {
					checkedElements.remove(selRt);
				}
				pcs.firePropertyChange(getName(), null, getValue());
			}
		});

		for (SeamRuntime rt : (List<SeamRuntime>) getValue()) {
			if (rt.isDefault()) {
				tableView.setChecked(rt, true);
				checkedElements.add(rt);
			}
		}
	}
	
	protected void createActionBar() {
		actionPanel = new ActionPanel(root, new BaseAction[] {
				new AddAction(), new EditAction(), new RemoveAction()});
		tableView.addSelectionChangedListener(actionPanel);
	}

	/**
	 * Checks all runtimes and set default one (for each version) if user did not do it. 
	 */
	private void setDefaultRuntimes() {
		List<SeamRuntime> runtimes = (List<SeamRuntime>)getValue();
		for (SeamRuntime seamRuntime : runtimes) {
			boolean checked = false;
			for(SeamRuntime checkedElement: checkedElements) {
				if(checkedElement.getVersion() == seamRuntime.getVersion()) {
					checked = true;
					break;
				}
			}
			if(!checked) {
				tableView.setChecked(seamRuntime, true);
				checkedElements.add(seamRuntime);
			}
		}
	}

	/**
	 * Return array of Controls that forms and editor
	 * 
	 * @return Control[]
	 */
	@Override
	public Object[] getEditorControls() {
		return new Control[] {root};
	}

	/**
	 * Return number of controls in editor
	 * 
	 * @return int
	 */
	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * Fill wizard page with editors
	 * 
	 * @param parent
	 *            Composite - parent composite
	 */
	@Override
	public void doFillIntoGrid(Object parent) {
		Assert
				.isTrue(
						parent instanceof Composite,
						SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_PARENT_CONTROL_SHOULD_BE_COMPOSITE);
		Assert
				.isTrue(
						((Composite) parent).getLayout() instanceof GridLayout,
						SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_EDITOR_SUPPORTS_ONLY_GRID_LAYOUT);
		Composite aComposite = (Composite) parent;
		Control[] controls = (Control[]) getEditorControls(aComposite);
		GridLayout gl = (GridLayout) ((Composite) parent).getLayout();

		GridData gd = new GridData();
		gd.horizontalSpan = gl.numColumns;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		((Control) getEditorControls()[0]).setLayoutData(gd);
	}

	/**
	 * Wizard page for editing Seam Runtime parameters
	 * 
	 * @author eskimo
	 */
	public static class SeamRuntimeWizardPage extends WizardPage implements
			PropertyChangeListener {

		private static final String SRT_NAME = "name"; //$NON-NLS-1$
		private static final String SRT_HOMEDIR = "homeDir"; //$NON-NLS-1$
		private static final String SRT_VERSION = "version"; //$NON-NLS-1$

		private static final int GL_PARENT_COLUMNS = 1;
		private static final int GL_CONTENT_COLUMNS = 3;

		List<SeamRuntime> value = null;

		IFieldEditor name = IFieldEditorFactory.INSTANCE.createTextEditor(
				SRT_NAME, SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME2,
				""); //$NON-NLS-1$ 

		IFieldEditor version = null;

		IFieldEditor homeDir = IFieldEditorFactory.INSTANCE
				.createBrowseFolderEditor(
						SRT_HOMEDIR,
						SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_HOME_FOLDER,
						""); //$NON-NLS-1$ 

		SeamRuntime current = null;
		List<SeamVersion> validSeamVersions = null;

		/**
		 * Create Seam Runtime editing wizard page with all SeamVersion in
		 * version selection combo
		 * 
		 * @param editedList
		 *            List&lt;SeamVersion&gt; - list of existing Seam Runtimes
		 */
		public SeamRuntimeWizardPage(List<SeamRuntime> editedList) {
			this(editedList, (List<SeamVersion>) null);
		}

		/**
		 * Create Seam Runtime editing wizard page with validSeamVersions in
		 * version selection combo
		 * 
		 * @param editedList
		 *            List&lt;SeamVersion&gt; - list of existing Seam Runtimes
		 * @param validSeamVersions
		 *            List&lt;SeamVersion&gt; - list of allowed Seam Versions
		 */
		public SeamRuntimeWizardPage(List<SeamRuntime> editedList,
				List<SeamVersion> validSeamVersions) {
			super(
					SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NEW_SEAM_RUNTIME);
			if (validSeamVersions == null) {
				this.version = IFieldEditorFactory.INSTANCE.createComboEditor(
						SRT_VERSION,
						SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION2,
						Arrays.asList(new String[] {
								SeamVersion.SEAM_1_2.toString(),
								SeamVersion.SEAM_2_0.toString(),
								SeamVersion.SEAM_2_1.toString(),
								SeamVersion.SEAM_2_2.toString()}),
						SeamVersion.SEAM_1_2.toString(), false);
				this.validSeamVersions = new ArrayList<SeamVersion>();
				this.validSeamVersions.add(SeamVersion.SEAM_1_2);
				this.validSeamVersions.add(SeamVersion.SEAM_2_0);
				this.validSeamVersions.add(SeamVersion.SEAM_2_1);
				this.validSeamVersions.add(SeamVersion.SEAM_2_2);
			} else {
				this.version = IFieldEditorFactory.INSTANCE.createComboEditor(
						SRT_VERSION,
						SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION2,
						validSeamVersions, SeamVersion.SEAM_1_2.toString(),
						false);
				this.validSeamVersions = validSeamVersions;
			}

			setMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CREATE_A_SEAM_RUNTIME);
			setTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_SEAM_RUNTIME);
			setImageDescriptor(ImageDescriptor.createFromFile(
					SeamFormWizard.class, "SeamWebProjectWizBan.png")); //$NON-NLS-1$
			value = editedList;
		}

		/**
		 * Wizard page for editing SeamRuntime were is only single version of
		 * SeamRuntime can be used
		 * 
		 * @param editedList
		 *            List&lt;SeamRuntime&gt; - TBD
		 * @param validVersion
		 *            SeamVersion - TBD
		 */
		public SeamRuntimeWizardPage(List<SeamRuntime> editedList,
				SeamVersion validVersion) {
			this(editedList, Arrays.asList(new SeamVersion[] {validVersion}));
		}

		/**
		 * Create Wizard page content
		 * 
		 * @param parent
		 *            Composite - parent composite
		 */
		public void createControl(Composite parent) {
			parent.setLayout(new GridLayout(GL_PARENT_COLUMNS, false));
			GridData dg = new GridData();
			dg.horizontalAlignment = GridData.FILL;
			dg.grabExcessHorizontalSpace = true;
			Composite root = new Composite(parent, SWT.NONE);
			root.setLayoutData(dg);
			GridLayout gl = new GridLayout(GL_CONTENT_COLUMNS, false);
			root.setLayout(gl);
			homeDir.doFillIntoGrid(root);
			homeDir.addPropertyChangeListener(this);
			name.doFillIntoGrid(root);
			name.addPropertyChangeListener(this);
			version.doFillIntoGrid(root);
			SeamVersion sv = SeamVersion.findByString(version.getValueAsString());
			if(!validSeamVersions.contains(sv)) {
				version.setValue(validSeamVersions.get(0));
			} else {
				version.setValue(version.getValue()); // Fire change listeners;
			}
			version.addPropertyChangeListener(this);
			setPageComplete(false);
			setControl(root);
		}

		/**
		 * Process evt: setup default values based on Seam Home folder and
		 * validate user input
		 * 
		 * @param evt
		 *            PropertyChangeEvent describes changes in wizard
		 */
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if ("homeDir".equals(evt.getPropertyName())) { //$NON-NLS-1$
				if (name.getValueAsString() == null
						|| "".equals(name.getValueAsString().trim())) { //$NON-NLS-1$
					String homeDirName = homeDir.getValueAsString();
					if (homeDirName != null && !"".equals(homeDirName.trim())) { //$NON-NLS-1$
						File folder = new File(homeDirName);
						homeDirName = folder.getName();
					}
					name.setValue(homeDirName);

					String seamVersion = SeamUtil.getSeamVersionFromManifest(homeDir.getValueAsString());
					if (seamVersion == null) {
						setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_FIND_JBOSS_SEAM_JAR);
						setPageComplete(false);
						return;
					}
					if (validSeamVersions != null) {
						for (SeamVersion ver : validSeamVersions) {
							if(SeamUtil.areSeamVersionsMatched(ver.toString(), seamVersion)) {
								version.setValue(ver.toString());
								break;
							}
						}
					}
				}
			}

			if (name.getValueAsString() == null || "".equals(//$NON-NLS-1$
					name.getValueAsString().toString().trim())) {
				setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME_CANNOT_BE_EMPTY);
				setPageComplete(false);
				return;
			}

			if (!name.getValueAsString().matches(
					"[a-zA-Z_][a-zA-Z0-9_\\-\\. ]*")) { //$NON-NLS-1$
				setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_RUNTIME_NAME_IS_NOT_CORRECT);
				setPageComplete(false);
				return;
			}
			for (SeamRuntime rt : value) {
				if (current != null && current.getName().equals(rt.getName())) {
					continue;
				}
				if (rt.getName().equals(name.getValueAsString())) {
					setErrorMessage(NLS.bind(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_RUNTIME_ALREADY_EXISTS,
							name.getValueAsString()));
					setPageComplete(false);
					return;
				}
			}

			if (current != null
					&& current.getName().equals(name.getValueAsString())
					&& current.getVersion().toString().equals(
							version.getValueAsString())
					&& current.getHomeDir().equals(homeDir.getValueAsString())) {
				setErrorMessage(null);
				setPageComplete(false);
				setMessage(null);
				return;
			}

			if (homeDir.getValueAsString() == null
					|| "".equals(homeDir.getValueAsString().trim())) { //$NON-NLS-1$
				setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_PATH_TO_SEAM_HOME_DIRECTORY_CANNOT_BE_EMPTY);
				setPageComplete(false);
				return;
			}

			String seamVersion = SeamUtil.getSeamVersionFromManifest(homeDir.getValueAsString());
			if (seamVersion == null) {
				setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_FIND_JBOSS_SEAM_JAR);
				setPageComplete(false);
				return;
			} else if ("".equals(seamVersion)) { //$NON-NLS-1$
				setMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_OBTAIN_SEAM_VERSION_NUMBER,
						IMessageProvider.WARNING);
				setPageComplete(true);
				return;
			} else if(!SeamUtil.areSeamVersionsMatched(version.getValueAsString(), seamVersion)) {
				String trimmedVersion = SeamUtil.trimSeamVersion(version.getValueAsString(), 1);
				String trimmedSeamVersion = SeamUtil.trimSeamVersion(seamVersion, 1);
				if(SeamVersion.findMatchingVersion(seamVersion)==null && SeamUtil.areSeamVersionsMatched(trimmedVersion, trimmedSeamVersion)) {
					setMessage(NLS.bind(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_THE_SELECTED_SEAM_APPEARS_TO_BE_OF_INCOMATIBLE_VERSION,
							seamVersion), IMessageProvider.WARNING);
				} else {
					setErrorMessage(NLS.bind(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_THE_SELECTED_SEAM_APPEARS_TO_BE_OF_INCOMATIBLE_VERSION,
						seamVersion));
					setPageComplete(false);
					return;
				}
			} else {
				setMessage(null);
			}

			Map<String, IStatus> errors = ValidatorFactory.JBOSS_SEAM_HOME_FOLDER_VALIDATOR
					.validate(homeDir.getValueAsString(), seamVersion);
			if (errors != ValidatorFactory.NO_ERRORS) {
				setErrorMessage(errors.get(
						ISeamFacetDataModelProperties.JBOSS_SEAM_HOME)
						.getMessage());
				setPageComplete(false);
				return;
			}

			setErrorMessage(null);
			setPageComplete(true);
		}

		/**
		 * Return Seam Runtime instance initialized by user input
		 * 
		 * @return SeamRuntime instance
		 */
		public SeamRuntime getRuntime() {
			SeamRuntime newRt = new SeamRuntime();
			newRt.setName(name.getValueAsString());
			newRt.setVersion(SeamVersion.parseFromString(version
					.getValueAsString()));
			newRt.setHomeDir(homeDir.getValueAsString());
			return newRt;
		}
	}

	/**
	 * Wizard collect information and creates new SeamRuntime instances.
	 * 
	 * @author eskimo
	 */
	public static class SeamRuntimeNewWizard extends Wizard {

		SeamRuntimeWizardPage page1 = null;
		List<SeamRuntime> added = null;
		List<SeamRuntime> value = null;

		/**
		 * Constructor for creating a SeamRuntime wizard with default title and
		 * one page to edit new Seam Runtime parameters as name, Seam version
		 * and path to Seam home folder. Wizard allows constraining the list of
		 * Seam version to handle creating of Seam Runtime form Seam Facet
		 * Installation page.
		 * 
		 * @param exist
		 *            list of exists SeamRuntimes, that will be used during
		 *            created SeamRuntime name validation
		 * @param added
		 *            List&lt;SeamRuntime&gt; - TBD
		 * @param validSeamVersions
		 *            List&lt;SeamRuntime&gt; - List of Seam Runtime versions
		 *            that can be created
		 */
		public SeamRuntimeNewWizard(List<SeamRuntime> exist,
				List<SeamRuntime> added, List<SeamVersion> validSeamVersions) {
			super();
			setWindowTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NEW_SEAM_RUNTIME);
			page1 = new SeamRuntimeWizardPage(exist, validSeamVersions);
			addPage(page1);
			this.value = exist;
			this.added = added;
		}

		/**
		 * Create wizard that shows all available seam version in version selection
		 * combobox
		 * 
		 * @param existing
		 *            List of existing Seam Runtimes
		 * @param added
		 *            TBD
		 */
		public SeamRuntimeNewWizard(List<SeamRuntime> existing,
				List<SeamRuntime> added) {
			this(existing, added, null);
		}

		/**
		 * Do finish steps
		 * 
		 * @return boolean
		 */
		@Override
		public boolean performFinish() {
			SeamRuntime rt = page1.getRuntime();
			added.add(rt);
			value.add(rt);
			return true;
		}
	}

	/**
	 * Wizard for editing Seam Runrtime parameters: name, version and path to
	 * home folder
	 * 
	 * @author eskimo
	 */
	public static class SeamRuntimeEditWizard extends Wizard {
		SeamRuntimeWizardPage page1 = null;
		List<SeamRuntime> added = null;
		Map<SeamRuntime, SeamRuntime> changed = null;
		List<SeamRuntime> value = null;
		SeamRuntime source = null;

		/**
		 * Constructor with almost all initialization parameters
		 * 
		 * @param existing
		 *            List&lt;SeamRuntime&gt; - edited list of Seam Runtimes
		 * @param source
		 *            SeamRuntime - edited Seam Runtime
		 * @param added
		 *            List&lt;SeamRuntime&gt; - TBD
		 * @param changed
		 *            List&lt;SeamRuntime&gt; - TBD
		 */
		public SeamRuntimeEditWizard(List<SeamRuntime> existing,
				SeamRuntime source, List<SeamRuntime> added,
				Map<SeamRuntime, SeamRuntime> changed) {
			super();
			setWindowTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_EDIT_SEAM_RUNTIME);
			page1 = new SeamRuntimeWizardPage(existing);
			page1
					.setMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_MODIFY_SEAM_RUNTIME);
			page1
					.setTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_EDIT_SEAM_RUNTIME);
			addPage(page1);
			this.value = existing;
			this.added = added;
			this.changed = changed;
			this.source = source;
			page1.name.setValue(source.getName());
			page1.homeDir.setValue(source.getHomeDir());
			page1.version.setValue(source.getVersion().toString());
			page1.current = source;
		}

		/**
		 * Perform operations to finish editing Seam Runtime parameters
		 * 
		 * @return boolean - always true
		 */
		@Override
		public boolean performFinish() {
			SeamRuntime rt = page1.getRuntime();
			if (rt.getName().equals(source.getName())
					&& rt.getVersion().toString().equals(
							source.getVersion().toString())
					&& rt.getHomeDir().equals(source.getHomeDir())) {
				return true;
			}
			if (added.contains(source) || changed.containsKey(source)) {
				source.setName(rt.getName());
				source.setHomeDir(rt.getName());
				source.setVersion(rt.getVersion());
			} else {
				changed.put(rt, source);
				int i = value.indexOf(source);
				if (i >= 0) {
					value.set(i, rt);
				} else {
					value.remove(source);
					value.add(rt);
				}
			}
			return true;
		}
	}

	/**
	 * Composite that holds list of BaseActions and presents them as column of
	 * buttons
	 * 
	 * @author eskimo
	 */
	public static class ActionPanel extends Composite implements
			ISelectionChangedListener {

		private BaseAction[] actions = null;

		/**
		 * Constructor creates panel with style, grid layout and buttons
		 * represented the actions
		 * 
		 * @param parent
		 *            Composite
		 * @param style
		 *            int
		 * @param actions
		 *            BaseAction[]
		 */
		public ActionPanel(Composite parent, int style, BaseAction[] actions) {
			super(parent, style);
			this.actions = actions;
			setLayout(new GridLayout(1, false));
			for (BaseAction action : this.actions) {
				new ActionButton(this, SWT.PUSH, action);
			}
		}

		/**
		 * Constructor creates panel with default style, grid layout and buttons
		 * represented the actions
		 * 
		 * @param parent
		 *            Composite
		 * @param actions
		 *            BaseAction[]
		 */
		public ActionPanel(Composite parent, BaseAction[] actions) {
			this(parent, SWT.NONE, actions);
		}

		/**
		 * Listen to the selection changes and update actions state
		 * (enable/disable)
		 * 
		 * @param event
		 *            SelectionChangeEvent
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			for (BaseAction action : actions) {
				action.setSelection(event.getSelection());
			}
		}
	}

	/**
	 * Class represents an BaseAction as SWT button control and runs action when
	 * button is prtessed
	 * 
	 * @author eskimo
	 */
	public static class ActionButton implements IPropertyChangeListener {

		private Button button;
		private BaseAction action;

		/**
		 * Create Button control with parent control and style that represents
		 * action
		 * 
		 * @param parent
		 *            Composite
		 * @param style
		 *            int
		 * @param action
		 *            BaseAction
		 */
		public ActionButton(Composite parent, int style, BaseAction action) {
			this.button = new Button(parent, style);
			this.action = action;

			GridData gd = new GridData(GridData.FILL_HORIZONTAL,
					GridData.CENTER, false, false);

			gd.horizontalAlignment = GridData.FILL;
			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
			this.button.setLayoutData(gd);
			this.action.addPropertyChangeListener(this);
			this.button.setText(action.getText());
			this.button.setEnabled(action.isEnabled());
			this.button.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					ActionButton.this.action.run();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}

		/**
		 * Return SWT button control that calls provided action
		 * 
		 * @return Control - button swt control
		 */
		public Control getControl() {
			return button;
		}

		/**
		 * Update enabled/disabled button state
		 * 
		 * @param event
		 *            PropertyChangeEvent
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(IAction.ENABLED)) {
				button.setEnabled(((Boolean) event.getNewValue())
						.booleanValue());
			}
		}
	}

	/**
	 * Action that changes state enable/disable based on current table selection
	 * 
	 * @author eskimo
	 */
	public abstract class BaseAction extends Action {

		SeamRuntime[] runtimes = new SeamRuntime[0];

		/**
		 * Constructor creates action with provided name
		 * 
		 * @param name
		 *            String - action name
		 */
		public BaseAction(String name) {
			super(name);
			updateEnablement();
		}

		/**
		 * Set current selection
		 * 
		 * @param selection
		 *            ISelection - selected items
		 */
		public void setSelection(ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				List<SeamRuntime> rts = new ArrayList<SeamRuntime>();
				for (Object rt : ((IStructuredSelection) selection).toArray()) {
					rts.add((SeamRuntime) rt);
				}
				runtimes = rts.toArray(new SeamRuntime[] {});
			} else {
				runtimes = new SeamRuntime[0];
			}
			updateEnablement();
		}

		protected abstract void updateEnablement();
	}

	/**
	 * Action that invokes New Seam Runtime Dialog
	 * 
	 * @author eskimo
	 */
	public class AddAction extends BaseAction {

		/**
		 * Constructior create Add action with default name
		 */
		public AddAction() {
			super(SeamUIMessages.SeamRuntimeListFieldEditor_ActionAdd);
			// This action is always available
			setEnabled(true);
		}

		/**
		 * Do nothing, because Add action should be always available
		 */
		@Override
		protected void updateEnablement() {
			// Add button is always available
		}

		/**
		 * Invoke New Seam Runtime Dialog
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			Wizard wiz = new SeamRuntimeNewWizard(
					(List<SeamRuntime>) getValue(), added);
			WizardDialog dialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wiz);
			dialog.open();
			tableView.refresh();
			setDefaultRuntimes();
		}
	}

	/**
	 * Action starts an editing selected Seam Runtime in Edit Seam Runtime
	 * dialog
	 * 
	 * @author eskimo
	 */
	public class EditAction extends BaseAction {

		/**
		 * Create EditAction with default name
		 * 
		 * @param text
		 */
		public EditAction() {
			super(SeamUIMessages.SeamRuntimeListFieldEditor_ActionEdit);
		}

		/**
		 * Edit action is enabled when the only Seam Runtime is selected
		 */
		@Override
		protected void updateEnablement() {
			// available when the only SeamRuntime is selected
			setEnabled(runtimes.length == 1);
		}

		/**
		 * Start editing selected Seam Runtime in Edit Seam Runtime Wizard
		 * Dialog
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			SeamRuntime selected = runtimes[0];
			Wizard wiz = new SeamRuntimeEditWizard(
					(List<SeamRuntime>) getValue(), runtimes[0], added, changed);
			WizardDialog dialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wiz);
			dialog.open();
			tableView.refresh();
			if (changed.containsValue(selected)) {
				SeamRuntime c = findChangedRuntime(selected);
				if (c != null) {
					tableView.setSelection(new StructuredSelection(c));
				}
			}
		}

		private SeamRuntime findChangedRuntime(SeamRuntime source) {
			for (SeamRuntime r : changed.keySet()) {
				if (source == changed.get(r)) {
					return r;
				}
			}
			return null;
		}
	}

	/**
	 * Action deletes all selected Seam Runtimes. A worning message is shown for
	 * used Seam Runtimes
	 * 
	 * @author eskimo
	 */
	public class RemoveAction extends BaseAction {
		
		/**
		 * Create DeleteAction action with default name
		 */
		public RemoveAction() {
			super(SeamUIMessages.SeamRuntimeListFieldEditor_ActionRemove);
		}

		@Override
		protected void updateEnablement() {
			setEnabled(runtimes.length > 0);
		}

		/**
		 * Remove all selected Seam Runtimes one by one
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			for (SeamRuntime rt : runtimes) {
				removeRuntime(rt);
			}
			tableView.refresh();
			setDefaultRuntimes();
		}

		private void removeRuntime(SeamRuntime r) {
			boolean used = isRuntimeUsed(r.getName());
			String title = SeamUIMessages.RUNTIME_DELETE_CONFIRM_TITLE;
			String message = (used) ? NLS.bind(
					SeamUIMessages.RUNTIME_DELETE_USED_CONFIRM, r.getName())
					: NLS.bind(SeamUIMessages.RUNTIME_DELETE_NOT_USED_CONFIRM,
							r.getName());
			boolean b = MessageDialog.openConfirm(tableView.getControl()
					.getShell(), title, message);
			if (b) {
				if (changed.containsKey(r)) {
					r = changed.remove(r);
				}
				removed.add(r);
				if (added.contains(r)) {
					added.remove(r);
				}
				((List) getValue()).remove(r);
			}
			checkedElements.remove(r);
		}

		private boolean isRuntimeUsed(String runtimeName) {
			IProject[] ps = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			for (int i = 0; i < ps.length; i++) {
				ISeamProject sp = SeamCorePlugin.getSeamProject(ps[i], false);
				if (sp != null && runtimeName.equals(sp.getRuntimeName())) {
					return true;
				}
			}
			return false;
		}
	}
}