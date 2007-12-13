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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import org.jboss.tools.common.model.ui.action.CommandBar;
import org.jboss.tools.common.model.ui.action.CommandBarListener;
import org.jboss.tools.common.model.ui.objecteditor.XChildrenEditor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard;

/**
 * @author eskimo
 *
 */
public class SeamRuntimeListFieldEditor extends BaseFieldEditor implements ISelectionChangedListener, SelectionListener, CommandBarListener {
	static String REMOVE = "&Remove";
	CheckboxTableViewer tableView = null;
	Composite root  = null;
	protected CommandBar bar = new CommandBar();
	/**
	 * @param name
	 * @param label
	 * @param defaultValue
	 */
	public SeamRuntimeListFieldEditor(String name, String label, Object defaultValue) {
		super(name, label, defaultValue);
		bar.addCommandBarListener(this);		
	}

	private SeamRuntime checkedElement = null;
	
	/**
	 * 
	 * @return
	 */
	public SeamRuntime getDefaultSeamRuntime() {
		return checkedElement;
	}
	
	private List<SeamRuntime> added = new ArrayList<SeamRuntime>();
	
	/**
	 * 
	 * @return
	 */
	public List<SeamRuntime> getAddedSeamRuntimes() {
		return added;
	}
	
	private Map<SeamRuntime,SeamRuntime> changed = new HashMap<SeamRuntime,SeamRuntime>();
	
	/**
	 * 
	 * @return
	 */
	public Map<SeamRuntime,SeamRuntime> getChangedSeamRuntimes() {
		return changed;
	}
	
	private List<SeamRuntime> removed = new ArrayList<SeamRuntime>();
	
	/**
	 * 
	 * @return
	 */
	public List<SeamRuntime> getRemoved() {
		return removed;
	}
	
	@Override
	public Object[] getEditorControls(Object composite) {
		
		root = new Composite((Composite)composite,SWT.NONE);
		root.setLayout(new GridLayout(2,false));
		GridData gd = new GridData();	
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;		
        root.setLayoutData(gd);
        
		tableView = CheckboxTableViewer.newCheckList(root, 
				        SWT.V_SCROLL|SWT.BORDER|SWT.FULL_SELECTION|SWT.SINGLE);
		
		gd = new GridData();
		gd.heightHint = 200;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
		tableView.getControl().setLayoutData(gd);
		tableView.addSelectionChangedListener(this);	
		
		createCommandBar(root);
		bar.setEnabled(REMOVE, false);
		bar.setEnabled(XChildrenEditor.EDIT, false);
		
		TableColumn tc1 = new TableColumn(tableView.getTable(),SWT.CENTER);
		tc1.setWidth(21);
		tc1.setResizable(false);
		
		TableColumn tc2 = new TableColumn(tableView.getTable(),SWT.LEFT);
		tc2.setWidth(100);
		tc2.setText(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME);	

		TableColumn tc3 = new TableColumn(tableView.getTable(),SWT.LEFT);
		tc3.setWidth(50);
		tc3.setText(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION);	
		
		TableColumn tc4 = new TableColumn(tableView.getTable(),SWT.LEFT);
		tc4.setWidth(100);
		tc4.setText(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_PATH);
		
		tableView.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if(inputElement instanceof List)
					return ((List<SeamRuntime>)inputElement).toArray();
				else
					throw new IllegalArgumentException(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_INPUTELEMENT_MUST_BE +
							SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_AN_INSTANCEOF_OF_LIST);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				viewer.refresh();
			}
		});

		tableView.setLabelProvider(new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {}

			public void dispose() {	}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				SeamRuntime rt = (SeamRuntime)element;
				if(columnIndex==0) return ""; //$NON-NLS-1$
				if(columnIndex==1) return rt.getName();
				if(columnIndex==2) return rt.getVersion().toString();
				if(columnIndex==3) return rt.getHomeDir();
				return ""; //$NON-NLS-1$
			}
		});
		
		tableView.setInput(getValue());
		tableView.getTable().setLinesVisible(true);
		tableView.getTable().setHeaderVisible(true);
		tableView.addCheckStateListener( new ICheckStateListener () {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if(event.getChecked()) {
					tableView.setCheckedElements(new Object[]{event.getElement()});
					//checkedElement.setDefault(false);
					checkedElement = (SeamRuntime)event.getElement();
					//checkedElement.setDefault(true);
				} else if(checkedElement==event.getElement()) {
						tableView.setCheckedElements(new Object[]{event.getElement()});
				}
				pcs.firePropertyChange(getName(), null, getValue());
			}
		});
		
		for (SeamRuntime rt : (List<SeamRuntime>) getValue()) {
			if(rt.isDefault()) {
				tableView.setCheckedElements(new Object[]{rt});
				checkedElement = rt;
				break;
			}
		}
		
		return new Control[]{tableView.getControl()};
	}

	@Override
	public Object[] getEditorControls() {
		return new Control[]{root};
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * 
	 * @param parent
	 */
	@Override
	public void doFillIntoGrid(Object parent) {
		Assert.isTrue(parent instanceof Composite, 
										SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_PARENT_CONTROL_SHOULD_BE_COMPOSITE);
		Assert.isTrue(((Composite)parent).getLayout() 
					instanceof GridLayout,SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_EDITOR_SUPPORTS_ONLY_GRID_LAYOUT);
		Composite aComposite = (Composite) parent;
		Control[] controls = (Control[])getEditorControls(aComposite);
		GridLayout gl = (GridLayout)((Composite)parent).getLayout();
		
		GridData gd = new GridData();
		gd.horizontalSpan = gl.numColumns;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		((Control)getEditorControls()[0]).setLayoutData(gd);
	}

	public static class SeamRuntimeWizardPage extends WizardPage implements PropertyChangeListener {

		List<SeamRuntime> value = null;
		
		IFieldEditor name = IFieldEditorFactory.INSTANCE.createTextEditor(
				"name", SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME2, ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		IFieldEditor version = null;
		
		IFieldEditor homeDir = IFieldEditorFactory.INSTANCE.createBrowseFolderEditor(
				"homeDir", SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_HOME_FOLDER, ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		IFieldEditor dflt = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				                           "default", SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_USE_AS_DEFAULT, false); //$NON-NLS-1$
		
		SeamRuntime current = null;
		List<SeamVersion> validSeamVersions = null;
		
		/**
		 * @param parent
		 * @param style
		 */
		public SeamRuntimeWizardPage(List<SeamRuntime> editedList) {
			this(editedList, null);
		}

		/**
		 * @param parent
		 * @param style
		 */
		public SeamRuntimeWizardPage(List<SeamRuntime> editedList, List<SeamVersion> validSeamVersions) {
			super(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NEW_SEAM_RUNTIME);
			if(validSeamVersions==null) {
				this.version = IFieldEditorFactory.INSTANCE.createComboEditor(
					"version", SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION2, Arrays.asList( //$NON-NLS-1$
							new String[]{SeamVersion.SEAM_1_2.toString(), SeamVersion.SEAM_2_0.toString()}), 
							                SeamVersion.SEAM_1_2.toString(), false);
				validSeamVersions = new ArrayList<SeamVersion>();
				validSeamVersions.add(SeamVersion.SEAM_1_2);
				validSeamVersions.add(SeamVersion.SEAM_2_0);							                
			} else {
				this.version = IFieldEditorFactory.INSTANCE.createComboEditor(
						"version", SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION2, validSeamVersions, 
								                SeamVersion.SEAM_1_2.toString(), false);
				this.validSeamVersions = validSeamVersions;								          
			}

			setMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CREATE_A_SEAM_RUNTIME);
			setTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_SEAM_RUNTIME);
			setImageDescriptor(ImageDescriptor.createFromFile(
					SeamFormWizard.class, "SeamWebProjectWizBan.png")); //$NON-NLS-1$
			value = editedList;
		}

		/**
		 * 
		 */
		public void createControl(Composite parent) {
			parent.setLayout(new GridLayout(1, false));
			GridData dg = new GridData();
			dg.horizontalAlignment = GridData.FILL;
			dg.grabExcessHorizontalSpace = true;
			Composite root = new Composite(parent, SWT.NONE);
			root.setLayoutData(dg);
			GridLayout gl = new GridLayout(3, false);
			root.setLayout(gl);
			homeDir.doFillIntoGrid(root);
			homeDir.addPropertyChangeListener(this);
			name.doFillIntoGrid(root);
			name.addPropertyChangeListener(this);
			version.doFillIntoGrid(root);
			version.addPropertyChangeListener(this);
			setPageComplete(false);
			setControl(root);
		}

		/**
		 * 
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			if ("homeDir".equals(evt.getPropertyName())) {
				if (name.getValueAsString() == null ||
						"".equals(name.getValueAsString().trim())) {
					String homeDirName = homeDir.getValueAsString();
					if (homeDirName != null && !"".equals(homeDirName.trim())) {
						File folder = new File(homeDirName);
						homeDirName = folder.getName();
					}
					name.setValue(homeDirName);

					String seamVersion = getSeamVersion(homeDir.getValueAsString());
					for (SeamVersion ver : validSeamVersions) {
						if (seamVersion.matches(ver.toString().replace(".","\\.")+".*")) {
							version.setValue(ver.toString());
							break;
						}
					}
				}
 			}
				if(name.getValueAsString()==null || "".equals( //$NON-NLS-1$
						name.getValueAsString().toString().trim())) {
					setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME_CANNOT_BE_EMPTY);
					setPageComplete(false);
					return;
				}
				
				if(!name.getValueAsString().matches("[a-zA-Z_][a-zA-Z0-9_\\-\\. ]*")) { //$NON-NLS-1$
					setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_RUNTIME_NAME_IS_NOT_CORRECT);
					setPageComplete(false);
					return;
				}
				for (SeamRuntime rt : value) {
					if(current != null && current.getName().equals(rt.getName())) continue;
					if(rt.getName().equals(name.getValueAsString())) {
						setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_RUNTIME+name.getValueAsString()+ SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_ALREADY_EXISTS);
						setPageComplete(false);
						return;	
					}
				}
				
				if(current != null && current.getName().equals(name.getValueAsString())
					&& current.getVersion().toString().equals(version.getValueAsString())
					&& current.getHomeDir().equals(homeDir.getValueAsString())
						) {
					setErrorMessage(null);
					setPageComplete(false);
					return;
				}

				if(homeDir.getValueAsString()==null || "".equals(homeDir.getValueAsString().trim())) { //$NON-NLS-1$
					setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_PATH_TO_SEAM_HOME_DIRECTORY_CANNOT_BE_EMPTY);
					setPageComplete(false);
					return;
				}			
				
				String seamVersion = getSeamVersion(homeDir.getValueAsString());
				if(seamVersion == null){
					setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_FIND_JBOSS_SEAM_JAR);
					setPageComplete(false);
					return;
				}else if("".equals(seamVersion)) { //$NON-NLS-1$
					setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_OBTAIN_SEAM_VERSION_NUMBER);
					setPageComplete(false);
					return;
				} else if(!seamVersion.matches(version.getValueAsString().replace(".","\\.")+".*")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					setErrorMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_THE_SELECTED_SEAM_APPEARS_TO_BE_OF_INCOMATIBLE_VERSION + seamVersion + "'"); //$NON-NLS-1$
					setPageComplete(false);
					return;
				}
				
				Map errors = ValidatorFactory.JBOSS_SEAM_HOME_FOLDER_VALIDATOR.validate(
						homeDir.getValueAsString(), seamVersion);
				if( errors != ValidatorFactory.NO_ERRORS) {
					setErrorMessage(errors.get(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString());
					setPageComplete(false);
					return;
				}
				
				
			setErrorMessage(null);
			setPageComplete(true);
		}
		
		public static String getSeamVersion(String path) {
			File seamJarFile = new File(path, "jboss-seam.jar"); //$NON-NLS-1$
			if(!seamJarFile.exists()) {
				seamJarFile = new File(path, "lib/jboss-seam.jar"); // hack to make it work for seam2
				if(!seamJarFile.exists()) return null;
			}
			InputStream str=null;
			ZipFile seamJar;
			try {
				seamJar = new ZipFile(seamJarFile);

				ZipFileStructureProvider provider = new ZipFileStructureProvider(seamJar);
				ZipEntry entry = seamJar.getEntry("META-INF/MANIFEST.MF"); //$NON-NLS-1$
				str = provider.getContents(entry);

				Properties manifest = new Properties();
				manifest.load(str);
				Object sv = manifest.get(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_SEAM_VERSION);
				return sv==null?"":sv.toString(); //$NON-NLS-1$
				
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_READ_JAR_FILE,e);
			} finally {
				if(str!=null)
					try {
						str.close();
					} catch (IOException e) {
						// nothing to do with that
					}
			}
			return ""; //$NON-NLS-1$
		}
		
		/**
		 * 
		 * @return
		 */
		public SeamRuntime getRuntime() {
			SeamRuntime newRt = new SeamRuntime();
			newRt.setName(name.getValueAsString());
			newRt.setVersion(SeamVersion.parseFromString(version.getValueAsString()));
			newRt.setHomeDir(homeDir.getValueAsString());
			//newRt.setDefault((Boolean)dflt.getValue());
			return newRt;
		}
	}
	
	public static class SeamRuntimeNewWizard extends Wizard {

		SeamRuntimeWizardPage page1 = null;
		List<SeamRuntime> added = null;
		List<SeamRuntime> value = null;

		public SeamRuntimeNewWizard(List<SeamRuntime> value, List<SeamRuntime> added, List<SeamVersion> validSeamVersions) {
			super();
			setWindowTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_NEW_SEAM_RUNTIME);
			page1 = new SeamRuntimeWizardPage(value, validSeamVersions);
			addPage(page1);
			this.value = value;
			this.added = added;
		}

		public SeamRuntimeNewWizard(List<SeamRuntime> value, List<SeamRuntime> added) {
			this(value, added, null);
		}
		
		@Override
		public boolean performFinish() {
			SeamRuntime rt = page1.getRuntime();
			added.add(rt);
			value.add(rt);
			return true;
		}
	}

	public static class SeamRuntimeEditWizard extends Wizard {
		SeamRuntimeWizardPage page1 = null;
		List<SeamRuntime> added = null;
		Map<SeamRuntime, SeamRuntime> changed = null;
		List<SeamRuntime> value = null;
		SeamRuntime source = null;
		public SeamRuntimeEditWizard(List<SeamRuntime> value, SeamRuntime source, List<SeamRuntime> added, Map<SeamRuntime, SeamRuntime> changed) {
			super();
			setWindowTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_EDIT_SEAM_RUNTIME);
			page1 = new SeamRuntimeWizardPage(value);
			page1.setMessage(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_MODIFY_SEAM_RUNTIME);
			page1.setTitle(SeamUIMessages.SEAM_RUNTIME_LIST_FIELD_EDITOR_EDIT_SEAM_RUNTIME);
			addPage(page1);
			this.value = value;
			this.added = added;
			this.changed = changed;
			this.source = source;
			page1.name.setValue(source.getName());
			page1.homeDir.setValue(source.getHomeDir());
			page1.version.setValue(source.getVersion().toString());
			page1.current = source;
		}
		
		@Override
		public boolean performFinish() {
			SeamRuntime rt = page1.getRuntime();
			if(rt.getName().equals(source.getName()) 
				&& rt.getVersion().toString().equals(source.getVersion().toString())
				&& rt.getHomeDir().equals(source.getHomeDir())
				) {
				return true;
			}
			if(added.contains(source) || changed.containsKey(source)) {
				source.setName(rt.getName());
				source.setHomeDir(rt.getName());
				source.setVersion(rt.getVersion());
			} else {
				changed.put(rt, source);
				int i = value.indexOf(source);
				if(i >= 0) {
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
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		selectionChanged((SeamRuntime)selection.getFirstElement());
	}

	/**
	 * @param firstElement
	 */
	public void selectionChanged(SeamRuntime selection) {
		if(selection == null) {
			bar.setEnabled(REMOVE, false);
			bar.setEnabled(XChildrenEditor.EDIT, false);
		} else {
			bar.setEnabled(REMOVE, true);
			bar.setEnabled(XChildrenEditor.EDIT, true);
		}
		if(selection==null 
				|| selection == SeamRuntimeManager.getInstance().getDefaultRuntime()) {
		} else {
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
	}
	
	private void removeRuntime(SeamRuntime r) {
		boolean used = isRuntimeUsed(r.getName());
		String title = SeamUIMessages.RUNTIME_DELETE_CONFIRM_TITLE;
		String message = (used)
			? NLS.bind(SeamUIMessages.RUNTIME_DELETE_USED_CONFIRM, r.getName())
			: NLS.bind(SeamUIMessages.RUNTIME_DELETE_NOT_USED_CONFIRM, r.getName());
		boolean b = MessageDialog.openConfirm(bar.getControl().getShell(), title, message);
		if(!b) return;
		if(changed.containsKey(r)) {
			r = changed.remove(r);
		}
		removed.add(r);
		if(added.contains(r)) {
			added.remove(r);
		}
		((List)getValue()).remove(r);
	}
	
	private boolean isRuntimeUsed(String runtimeName) {
		IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < ps.length; i++) {
			ISeamProject sp = SeamCorePlugin.getSeamProject(ps[i], false);
			if(sp != null && runtimeName.equals(sp.getRuntimeName())) return true;
		}
		return false;
	}

	public void action(String command) {
		if(XChildrenEditor.ADD.equals(command)) {
			Wizard wiz = new SeamRuntimeNewWizard((List<SeamRuntime>)getValue(), added);
			WizardDialog dialog  = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
			dialog.open();
			tableView.refresh();
		} else if(REMOVE.equals(command)) {
			ISelection s = tableView.getSelection();
			if(s == null || s.isEmpty() || !(s instanceof IStructuredSelection)) return;
			IStructuredSelection ss = (IStructuredSelection)s;
			Iterator<?> i = ss.iterator();
			while(i.hasNext()) {
				Object o = i.next();
				if(o instanceof SeamRuntime) {
					removeRuntime((SeamRuntime)o);
				}
			}
			tableView.refresh();
		} else if(XChildrenEditor.EDIT.equals(command)) {
			ISelection s = tableView.getSelection();
			if(s == null || s.isEmpty() || !(s instanceof IStructuredSelection)) return;
			IStructuredSelection ss = (IStructuredSelection)s;
			Iterator<?> i = ss.iterator();
			while(i.hasNext()) {
				Object o = i.next();
				if(o instanceof SeamRuntime) {
					Wizard wiz = new SeamRuntimeEditWizard((List<SeamRuntime>)getValue(), (SeamRuntime)o, added, changed);
					WizardDialog dialog  = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
					dialog.open();
					tableView.refresh();
					if(changed.containsValue(o)) {
						SeamRuntime c = findChangedRuntime((SeamRuntime)o);
						if(c != null) {
							tableView.setSelection(new StructuredSelection(c));
						}
					}
				}
			}
		}
	}
	
	private SeamRuntime findChangedRuntime(SeamRuntime source) {
		for(SeamRuntime r: changed.keySet()) {
			if(source == changed.get(r)) return r;
		}
		return null;
	}
	
	public void dispose() {
		bar.dispose();
		super.dispose();
	}

	protected void createCommandBar(Composite control) {
		//TODO insert EDIT command
		String[] commands = new String[]{XChildrenEditor.ADD, XChildrenEditor.EDIT, REMOVE};
		bar.setCommands(commands);
		bar.getLayout().direction = SWT.VERTICAL;
		bar.getLayout().buttonWidth = convertHorizontalDLUsToPixels(control, IDialogConstants.BUTTON_WIDTH);
		setMargins(bar);
		bar.createControl(control);
		bar.getControl().setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}
	
	protected void setMargins(CommandBar bar) {
		bar.getLayout().setMargins(0,10,0,0);
	}

	protected int convertHorizontalDLUsToPixels(Control control, int dlus) {
		GC gc= new GC(control);
		gc.setFont(control.getFont());
		int averageWidth= gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();
	
		double horizontalDialogUnitSize = averageWidth * 0.25;
	
		return (int)Math.round(dlus * horizontalDialogUnitSize);
	}
	
}
