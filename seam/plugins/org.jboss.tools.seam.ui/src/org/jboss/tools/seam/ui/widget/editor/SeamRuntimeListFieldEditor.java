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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard;

/**
 * @author eskimo
 *
 */
public class SeamRuntimeListFieldEditor extends BaseFieldEditor implements ISelectionChangedListener, SelectionListener {

	CheckboxTableViewer tableView = null;
	Composite root  = null;
	Button rmBtn = null;
	Button addBtn = null;
	/**
	 * @param name
	 * @param label
	 * @param defaultValue
	 */
	public SeamRuntimeListFieldEditor(String name, String label, Object defaultValue) {
		super(name, label, defaultValue);
		
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
        
		tableView = CheckboxTableViewer.newCheckList((Composite) root, 
				        SWT.V_SCROLL|SWT.BORDER|SWT.FULL_SELECTION|SWT.SINGLE);
		
		gd = new GridData();
		gd.heightHint = 200;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
		tableView.getControl().setLayoutData(gd);
		tableView.addSelectionChangedListener(this);	
		
		Composite buttons = new Composite(root,SWT.NONE);	
		buttons.setLayout(new GridLayout(1,false));
		gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessVerticalSpace = true;
		buttons.setLayoutData(gd);
		
		addBtn = new Button(buttons,SWT.PUSH);
		addBtn.setText("Add");
		gd = new GridData(GridData.FILL_HORIZONTAL,GridData.CENTER,false,false);
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        gd.widthHint = 50;
        
		addBtn.setLayoutData(gd);
		addBtn.addSelectionListener(this);
		
		rmBtn = new Button(buttons,SWT.PUSH);
		rmBtn.setText("Remove");
		gd = new GridData(GridData.FILL_HORIZONTAL,GridData.CENTER,false,false);
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		rmBtn.setLayoutData(gd);
		rmBtn.addSelectionListener(this);
		
		/*final Button editBtn = new Button(buttons,SWT.PUSH);
		editBtn.setText("Edit");
		gd = new GridData(GridData.FILL_HORIZONTAL,GridData.CENTER,false,false);
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		editBtn.setLayoutData(gd);*/
		
		TableColumn tc1 = new TableColumn(tableView.getTable(),SWT.CENTER);
		tc1.setWidth(21);
		tc1.setResizable(false);
		
		TableColumn tc2 = new TableColumn(tableView.getTable(),SWT.LEFT);
		tc2.setWidth(100);
		tc2.setText("Name");	

		TableColumn tc3 = new TableColumn(tableView.getTable(),SWT.LEFT);
		tc3.setWidth(50);
		tc3.setText("Version");	
		
		TableColumn tc4 = new TableColumn(tableView.getTable(),SWT.LEFT);
		tc4.setWidth(100);
		tc4.setText("Path");
		
		tableView.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if(inputElement instanceof List)
					return ((List<SeamRuntime>)inputElement).toArray();
				else
					throw new IllegalArgumentException("inputElement must be " +
							"n instance of List<SeamRuntime>.");
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
				if(columnIndex==0) return "";
				if(columnIndex==1) return rt.getName();
				if(columnIndex==2) return rt.getVersion().toString();
				if(columnIndex==3) return rt.getHomeDir();
				return "";
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
	public void doFillIntoGrid(Object parent) {
		Assert.isTrue(parent instanceof Composite, 
										"Parent control should be Composite");
		Assert.isTrue(((Composite)parent).getLayout() 
					instanceof GridLayout,"Editor supports only grid layout");
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
		
		IFieldEditor name = SwtFieldEditorFactory.INSTANCE.createTextEditor(
				"name", "Name:", "");
		
		IFieldEditor version = SwtFieldEditorFactory.INSTANCE.createComboEditor(
				"version", "Version:", Arrays.asList(
						new Object[]{SeamVersion.SEAM_1_2.toString()}), 
						                SeamVersion.SEAM_1_2.toString(), true);
		
		IFieldEditor homeDir = SwtFieldEditorFactory.INSTANCE.createBrowseFolderEditor(
				"homeDir", "Home Folder:", "");
		
		IFieldEditor dflt = SwtFieldEditorFactory.INSTANCE.createCheckboxEditor(
				                           "default", "Use as default:", false);
		
		/**
		 * @param parent
		 * @param style
		 */
		public SeamRuntimeWizardPage(List<SeamRuntime> editedList) {
			super("New Seam Runtime");
			setMessage("Create a Seam Runtime");
			setTitle("Seam Runtime");
			setImageDescriptor(ImageDescriptor.createFromFile(
					SeamFormWizard.class, "SeamWebProjectWizBan.png"));
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
			name.doFillIntoGrid(root);
			name.addPropertyChangeListener(this);
			version.doFillIntoGrid(root);
			version.addPropertyChangeListener(this);
			homeDir.doFillIntoGrid(root);
			homeDir.addPropertyChangeListener(this);
			setPageComplete(false);
			setControl(root);
		}

		/**
		 * 
		 */
		public void propertyChange(PropertyChangeEvent evt) {

				if(name.getValueAsString()==null || "".equals(
						name.getValueAsString().toString().trim())) {
					setErrorMessage("Name cannot be empty");
					setPageComplete(false);
					return;
				}
				
				if(!name.getValueAsString().matches("[a-zA-Z_][a-zA-Z0-9_\\-\\. ]*")) {
					setErrorMessage("Runtime name is not correct");
					setPageComplete(false);
					return;
				}
				for (SeamRuntime rt : value) {
					if(rt.getName().equals(name.getValueAsString())) {
						setErrorMessage("Runtime '"+name.getValueAsString()+ "' already exists");
						setPageComplete(false);
						return;	
					}
				}				

				if(homeDir.getValueAsString()==null || "".equals(homeDir.getValueAsString().trim())) {
					setErrorMessage("Path to seam home directory cannot be empty");
					setPageComplete(false);
					return;
				}			
				
				Map errors = ValidatorFactory.JBOSS_SEAM_HOME_FOLDER_VALIDATOR.validate(
						homeDir.getValueAsString(), null);
				if( errors != ValidatorFactory.NO_ERRORS) {
					setErrorMessage(errors.get(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString());
					setPageComplete(false);
					return;
				}
				String seamVersion = getSeamVersion(homeDir.getValueAsString());
				if("".equals(seamVersion)) {
					setErrorMessage("Cannot obtain Seam version number from jboss-seam.jar file.");
					setPageComplete(false);
					return;
				} else if(!seamVersion.matches(version.getValueAsString()+".*")) {
					setErrorMessage("Selected seam has wrong version number '" + seamVersion + "'");
					setPageComplete(false);
					return;
				}
				
			setErrorMessage(null);
			setPageComplete(true);
		}
		
		public static String getSeamVersion(String path) {
			File seamJarFile = new File(path, "jboss-seam.jar");
			InputStream str=null;
			ZipFile seamJar;
			try {
				seamJar = new ZipFile(seamJarFile);

				ZipFileStructureProvider provider = new ZipFileStructureProvider(seamJar);
				ZipEntry entry = seamJar.getEntry("META-INF/MANIFEST.MF");
				str = provider.getContents(entry);

				Properties manifest = new Properties();
				manifest.load(str);
				Object sv = manifest.get("Seam-Version");
				return sv==null?"":sv.toString();
				
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError("Cannot read jboss-seam.jar file",e);
			} finally {
				if(str!=null)
					try {
						str.close();
					} catch (IOException e) {
						// nothing to do with that
					}
			}
			return "";
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
		public SeamRuntimeNewWizard(List<SeamRuntime> value, List<SeamRuntime> added) {
			super();
			setWindowTitle("New Seam Runtime");
			page1 = new SeamRuntimeWizardPage(value);
			addPage(page1);
			this.value = value;
			this.added = added;
		}
		
		public boolean performFinish() {
			added.add(page1.getRuntime());
			value.add(page1.getRuntime());
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
		if(e.widget==addBtn) {
			Wizard wiz = new SeamRuntimeNewWizard((List<SeamRuntime>)getValue(), added);
			WizardDialog dialog  = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
			dialog.open();
			tableView.refresh();
		}
	}
}
