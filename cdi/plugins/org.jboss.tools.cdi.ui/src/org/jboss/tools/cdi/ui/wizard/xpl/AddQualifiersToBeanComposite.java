/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Exadel, Inc.
 *     Red Hat, Inc.
 *******************************************************************************/
package org.jboss.tools.cdi.ui.wizard.xpl;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SearchPattern;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;
import org.jboss.tools.cdi.ui.wizard.AbstractModifyInjectionPointWizard;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewQualifierCreationWizard;

public class AddQualifiersToBeanComposite extends Composite {
	private static Font font;
	private IInjectionPoint injectionPoint;
	private IBean bean;
	private java.util.List<IBean> beans;
	private AddQualifiersToBeanWizardPage page;
	private Text pattern;

	// original qualifiers on the bean without declarations it means they can not be deleted from bean
	private ArrayList<ValuedQualifier> originalQualifiers = new ArrayList<ValuedQualifier>();

	// qualifiers available to be added to the bean
	private ArrayList<ValuedQualifier> qualifiers = new ArrayList<ValuedQualifier>();

	// original qualifiers on the bean with declaration + currently added qualifiers on the bean
	private ArrayList<ValuedQualifier> deployed = new ArrayList<ValuedQualifier>();
	
	private TableViewer availableTableViewer;
	private TableViewer deployedTableViewer;

	private Button add, addAll;
	private Button remove, editQualifierValue, removeAll;
	
	private Label nLabel;
	
	protected boolean isComplete = false;
	
	private boolean hasDefaultQualifier = false;
	
	private ValuedQualifier defaultQualifier, namedQualifier;
	
	private ILabelProvider labelProvider = new QualifiersListLabelProvider();

	public AddQualifiersToBeanComposite(Composite parent, AddQualifiersToBeanWizardPage page) {
		super(parent, SWT.NONE);
		this.page = page;
		this.injectionPoint = ((AbstractModifyInjectionPointWizard)page.getWizard()).getInjectionPoint();
		this.bean = ((AbstractModifyInjectionPointWizard)page.getWizard()).getSelectedBean();
		this.beans = ((AbstractModifyInjectionPointWizard)page.getWizard()).getBeans();
		
		createControl();
		if(bean != null)
			init(bean);
		
		page.setDeployedQualifiers(getDeployedQualifiers());
	}
	
	public void init(IBean bean){
		this.bean = bean;
		originalQualifiers.clear();
		deployed.clear();
		for(IQualifier q : bean.getQualifiers()){
			IQualifierDeclaration declaration = MarkerResolutionUtils.findQualifierDeclaration(bean, q);
			if(declaration != null){
				String value = MarkerResolutionUtils.findQualifierValue(declaration);
				ValuedQualifier vq = new ValuedQualifier(q, value);
				deployed.add(vq);
			}else{
				originalQualifiers.add(new ValuedQualifier(q, ""));
			}
		}
		
		defaultQualifier = new ValuedQualifier(bean.getCDIProject().getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME));
		namedQualifier = new ValuedQualifier(bean.getCDIProject().getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME));
		
		for(ValuedQualifier q : originalQualifiers){
			if(q.equals(defaultQualifier)){
				hasDefaultQualifier = true;
				break;
			}
		}
		
		ArrayList<ValuedQualifier> total = new ArrayList<ValuedQualifier>();
		total.addAll(originalQualifiers);
		total.addAll(deployed);
		
		deployedTableViewer.setInput(total);
		
		qualifiers.clear();
		
		loadAvailableQualifiers();
		
		availableTableViewer.setInput(qualifiers);
		if(nLabel != null)
			nLabel.setText(MessageFormat.format(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE,
					new Object[]{bean.getElementName()}));
		refresh();
	}
	
	private ValuedQualifier loadAvailableQualifiers(){
		ValuedQualifier lastQualifier = null;
		String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
		String beanPackage = beanTypeName.substring(0,beanTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
		IJavaProject beanJavaProject = bean.getBeanClass().getJavaProject();
		
		String injectionPointTypeName = injectionPoint.getClassBean().getBeanClass().getFullyQualifiedName();
		String injectionPointPackage = injectionPointTypeName.substring(0,injectionPointTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
		IJavaProject injectionPointJavaProject = injectionPoint.getBean().getBeanClass().getJavaProject();
		
		boolean samePackage = beanPackage.equals(injectionPointPackage);
		
		IQualifier[] qs = bean.getCDIProject().getQualifiers();
		
		for(IQualifier q : qs){
			ValuedQualifier vq = new ValuedQualifier(q);
			if(!contains(originalQualifiers, vq) && !contains(qualifiers, vq) && !contains(deployed, vq)){
				try{
					boolean isPublic = Flags.isPublic(q.getSourceType().getFlags());
					
					String qualifierTypeName = q.getSourceType().getFullyQualifiedName();
					String qualifierPackage = qualifierTypeName.substring(0,qualifierTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
					if((isPublic || (samePackage && injectionPointPackage.equals(qualifierPackage))) ){
						if(beanJavaProject.findType(qualifierTypeName) != null && injectionPointJavaProject.findType(qualifierTypeName) != null){
							qualifiers.add(vq);
							availableTableViewer.add(vq);
							lastQualifier = vq;
						}
					}
				} catch (JavaModelException ex) {
					CDIUIPlugin.getDefault().logError(ex);
				}
				
			}
		}
		return lastQualifier;
	}
	
	private boolean contains(ArrayList<ValuedQualifier> qualifiers, ValuedQualifier qualifier){
		for(ValuedQualifier q : qualifiers){
			if(q.equals(qualifier))
				return true;
		}
		return false;
	}

	public void refresh() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (availableTableViewer == null || availableTableViewer.getControl().isDisposed())
					return;
				availableTableViewer.refresh();
				deployedTableViewer.refresh();
				setEnablement();
			}
		});
	}
	
	public boolean checkBeans(){
		ArrayList<ValuedQualifier> total = new ArrayList<ValuedQualifier>();
		total.addAll(originalQualifiers);
		total.addAll(deployed);
		HashSet<ValuedQualifier> qfs = new HashSet<ValuedQualifier>(total);
		
		for(IBean b: beans){
			if(b.equals(bean))
				continue;
			if(MarkerResolutionUtils.checkValuedQualifiers(bean, b, qfs))
				return false;
				
		}
		return true;
	}
	
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 10;
		layout.numColumns = 3;
		setLayout(layout);
		setFont(getParent().getFont());
		
		Display display = getDisplay();
		FontData[] fd = getFont().getFontData();
		int size2 = fd.length;
		for (int i = 0; i < size2; i++)
			fd[i].setStyle(SWT.ITALIC);
		font = new Font(display, fd);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}
		});
		
		nLabel = new Label(this, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		nLabel.setLayoutData(data);
		if(bean != null)
			nLabel.setText(MessageFormat.format(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE,
				new Object[]{bean.getElementName()}));
		
		Label label = new Label(this, SWT.NONE);
		label.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_ENTER_QUALIFIER_NAME);
		label.setLayoutData(data);
		
		pattern = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		pattern.setLayoutData(data);
		pattern.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				availableTableViewer.refresh();
			}
		});
		pattern.setFocus();
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		label = new Label(this, SWT.NONE);
		label.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_AVAILABLE);
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		label = new Label(this, SWT.NONE);
		label.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_IN_BEAN);
		
		Table availableTable = new Table(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.widthHint = 150;
		availableTable.setLayoutData(data);
		
		availableTableViewer = new TableViewer(availableTable);
		
		availableTableViewer.setLabelProvider(labelProvider);
		IContentProvider contentProvider = new QualifiersListContentProvider();
		availableTableViewer.setContentProvider(contentProvider);
		availableTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object o1, Object o2) {
				if (o1 instanceof IQualifier && o2 instanceof IQualifier) {
					IQualifier q1 = (IQualifier) o1;
					IQualifier q2 = (IQualifier) o2;
					return (q1.getSourceType().getElementName().compareToIgnoreCase(q2.getSourceType().getElementName()));
				}
				
				return super.compare(viewer, o1, o2);
			}
		});
		availableTableViewer.setInput(qualifiers);
		
		availableTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setEnablement();
			}
		});
		availableTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				setEnablement();
				if (add.isEnabled())
					add(false);
			}
		});
		availableTableViewer.addFilter(new QualifierFilter());
		
		Composite comp = new Composite(this, SWT.NONE);
		data = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 120;
		comp.setLayoutData(data);
		
		layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 25;
		layout.verticalSpacing = 20;
		comp.setLayout(layout);
		
		add = new Button(comp, SWT.PUSH);
		add.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_ADD);
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(false);
			}
		});
		
		remove = new Button(comp, SWT.PUSH);
		remove.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_REMOVE);
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(false);
			}
		});
		
		label = new Label(comp, SWT.NONE);
		label.setText("");
		
		addAll = new Button(comp, SWT.PUSH);
		addAll.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_ADD_ALL);
		addAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(true);
			}
		});
		
		removeAll = new Button(comp, SWT.PUSH);
		removeAll.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_REMOVE_ALL);
		removeAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(true);
			}
		});
		
		Table deployedTable = new Table(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 150;
		deployedTable.setLayoutData(data);
		
		deployedTableViewer = new TableViewer(deployedTable);
		deployedTableViewer.setLabelProvider(labelProvider);
		deployedTableViewer.setContentProvider(contentProvider);
		deployedTableViewer.setComparator(new ViewerComparator() {
		public int compare(Viewer viewer, Object o1, Object o2) {
			if (o1 instanceof IQualifier && o2 instanceof IQualifier) {
				IQualifier q1 = (IQualifier) o1;
				IQualifier q2 = (IQualifier) o2;
				return (q1.getSourceType().getElementName().compareToIgnoreCase(q2.getSourceType().getElementName()));
			}
			
			return super.compare(viewer, o1, o2);
			}
		});
		deployedTableViewer.setInput(originalQualifiers);
		
		deployedTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setEnablement();
			}
		});
		deployedTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				setEnablement();
				if (remove.isEnabled())
					remove(false);
			}
		});
		
		final Button createQualifier = new Button(this, SWT.PUSH);
		createQualifier.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_CREATE_NEW_QUALIFIER);
		
		createQualifier.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final IJobManager manager= Job.getJobManager();
				// reload qualifiers
				if (Display.getCurrent() != null) {
					BusyIndicator.showWhile(Display.getCurrent(), new Runnable(){
						public void run(){
							manager.endRule(ResourcesPlugin.getWorkspace().getRoot());
							
							NewQualifierCreationWizard wizard = new NewQualifierCreationWizard();
							StructuredSelection selection = new StructuredSelection(new Object[]{bean.getBeanClass()});
							
							wizard.init(PlatformUI.getWorkbench(), selection);
							WizardDialog dialog = new WizardDialog(shell, wizard);
							int status = dialog.open();
							if(status == WizardDialog.OK){
								try {
									Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
								} catch (OperationCanceledException e) {
									CDICorePlugin.getDefault().logError(e);
								} catch (InterruptedException e) {
									CDICorePlugin.getDefault().logError(e);
								}
								loadAvailableQualifiers();
							}
							manager.beginRule(ResourcesPlugin.getWorkspace().getRoot(), null);
						}
						
					});
				}
			}
		});
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		editQualifierValue = new Button(this, SWT.PUSH);
		editQualifierValue.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_EDIT_QUALIFIER_VALUE);
		
		editQualifierValue.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ValuedQualifier[] vq = getDeployedSelection();
				ValueDialog d = new ValueDialog(getShell(), vq[0].getValue());
				int result = d.open();
				if(result == MessageDialog.OK){
					vq[0].setValue(d.getValue());
				}
			}
		});
		
		setEnablement();
		
		Dialog.applyDialogFont(this);
	}

	protected ValuedQualifier[] getAvailableSelection() {
		IStructuredSelection sel = (IStructuredSelection) availableTableViewer.getSelection();
		if (sel.isEmpty())
			return new ValuedQualifier[0];
			
		ValuedQualifier[]  mss = new ValuedQualifier[sel.size()];
		System.arraycopy(sel.toArray(), 0, mss, 0, sel.size());
		return mss;
	}

	protected ValuedQualifier[] getDeployedSelection() {
		IStructuredSelection sel = (IStructuredSelection) deployedTableViewer.getSelection();
		if (sel.isEmpty())
			return new ValuedQualifier[0];
		
		ValuedQualifier[]  mss = new ValuedQualifier[sel.size()];
		System.arraycopy(sel.toArray(), 0, mss, 0, sel.size());
		return mss;
	}

	protected void setEnablement() {
		isComplete = true;
		
		ValuedQualifier[] ms = getAvailableSelection();
		if (ms == null ||  ms.length == 0) {
			add.setEnabled(false);
		} else {
			boolean enabled = false;
			for (int i = 0; i < ms.length; i++) {
				ValuedQualifier qualifier = ms[i];
				if (qualifier != null) {
					if (contains(qualifiers, qualifier)) {
						enabled = true;
					}else{
						enabled = false;
						break;
					}
				}
			}
			add.setEnabled(enabled);
		}
		addAll.setEnabled(qualifiers.size() > 0);
		
		ms = getDeployedSelection();
		if (ms == null ||  ms.length == 0) {
			remove.setEnabled(false);
			editQualifierValue.setEnabled(false);
		} else {
			boolean enabled = false;
			for (int i = 0; i < ms.length; i++) {
				ValuedQualifier qualifier = ms[i];
				if (qualifier != null && contains(deployed, qualifier)) {
					enabled = true;
				}
				else{
					enabled = false;
					break;
				}
			}
			remove.setEnabled(enabled);
			
			if(enabled && ms.length == 1 && isEditEnabled(ms[0].qualifier)){
				editQualifierValue.setEnabled(true);
			}else{
				editQualifierValue.setEnabled(false);
			}
		}
		removeAll.setEnabled(deployed.size() > 0);
		
		// check uniqueness of qualifiers
		isComplete = checkBeans();
		if(isComplete)
			page.setMessage("");
		else
			page.setMessage(NLS.bind(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_SET_IS_NOT_UNIQUE, bean.getElementName(), injectionPoint.getElementName()), IMessageProvider.ERROR);
		
		page.setPageComplete(isComplete);
	}
	
	private boolean isEditEnabled(IQualifier qualifier){
		IMethod method = qualifier.getSourceType().getMethod("value", new String[]{});
		try{
			if(method.exists()){
				if(method.getReturnType().equals("Ljava.lang.String;"))
					return true;
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		
		return false;
	}
	
	protected void add(boolean all) {
		if (all) {
			ValuedQualifier[] qualifiers2 = new ValuedQualifier[qualifiers.size()];
			qualifiers.toArray(qualifiers2);
			moveAll(qualifiers2, true);
		} else
			moveAll(getAvailableSelection(), true);
	}

	protected void remove(boolean all) {
		if (all) {
			ArrayList<ValuedQualifier> list = new ArrayList<ValuedQualifier>();
			list.addAll(deployed);
			
			ValuedQualifier[] qualifiers2 = new ValuedQualifier[list.size()];
			list.toArray(qualifiers2);
			
			moveAll(qualifiers2, false);
		} else
			moveAll(getDeployedSelection(), false);
	}

	protected void moveAll(ValuedQualifier[] mods, boolean add2) {
		int size = mods.length;
		ArrayList<ValuedQualifier> list = new ArrayList<ValuedQualifier>();
		for (int i = 0; i < size; i++) {
			if (!contains(list, mods[i]))
				list.add(mods[i]);
		}
		
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			ValuedQualifier qualifier = (ValuedQualifier) iterator.next();
			if (add2) {
				qualifiers.remove(qualifier);
				deployed.add(qualifier);
				availableTableViewer.remove(qualifier);
				deployedTableViewer.add(qualifier);
			} else {
				qualifiers.add(qualifier);
				deployed.remove(qualifier);
				availableTableViewer.add(qualifier);
				deployedTableViewer.remove(qualifier);
			}
		}
		
		if(hasDefaultQualifier){
			if(deployed.isEmpty() || (namedQualifier != null && deployed.size() == 1 && deployed.contains(namedQualifier))) {
				if(!originalQualifiers.contains(defaultQualifier)){
					originalQualifiers.add(defaultQualifier);
					deployedTableViewer.add(defaultQualifier);
				}
			}else{
				if(originalQualifiers.contains(defaultQualifier)){
					originalQualifiers.remove(defaultQualifier);
					deployedTableViewer.remove(defaultQualifier);
				}
			}
		}

		setEnablement();
		page.setDeployedQualifiers(getDeployedQualifiers());
	}


	public ArrayList<ValuedQualifier> getQualifiersToRemove() {
		ArrayList<ValuedQualifier> list = new ArrayList<ValuedQualifier>();
		Iterator iterator = originalQualifiers.iterator();
		while (iterator.hasNext()) {
			ValuedQualifier qualifier = (ValuedQualifier) iterator.next();
			if (!contains(deployed, qualifier))
				list.add(qualifier);
		}
		return list;
	}

	public ArrayList<ValuedQualifier> getQualifiersToAdd() {
		ArrayList<ValuedQualifier> list = new ArrayList<ValuedQualifier>();
		Iterator iterator = deployed.iterator();
		while (iterator.hasNext()) {
			ValuedQualifier qualifier = (ValuedQualifier) iterator.next();
			if (!contains(originalQualifiers, qualifier))
				list.add(qualifier);
		}
		return list;
	}

	public boolean isComplete() {
		return isComplete;
	}
	
	public ArrayList<ValuedQualifier> getDeployedQualifiers(){
		ArrayList<ValuedQualifier> total = new ArrayList<ValuedQualifier>();
		total.addAll(originalQualifiers);
		total.addAll(deployed);
		
		return total;
	}
	
	public void deploy(ValuedQualifier qualifier){
		ValuedQualifier[] qualifiers = new ValuedQualifier[]{qualifier};
		moveAll(qualifiers, true);
	}

	public void remove(ValuedQualifier qualifier){
		ValuedQualifier[] qualifiers = new ValuedQualifier[]{qualifier};
		moveAll(qualifiers, false);
	}

	public ArrayList<IQualifier> getAvailableQualifiers(){
		ArrayList<IQualifier> result = new ArrayList<IQualifier>();
		for(ValuedQualifier vq : qualifiers){
			result.add(vq.getQualifier());
		}
		
		return result;
	}

	class QualifiersListLabelProvider implements ILabelProvider, IColorProvider{

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getImage(Object element) {
			if(element instanceof ValuedQualifier){
				return CDIImages.getImageByElement(((ValuedQualifier) element).qualifier);
			}
			return null;
		}

		public String getText(Object element) {
			if(element instanceof ValuedQualifier){
				ValuedQualifier vq = (ValuedQualifier)element;
				String qualifierTypeName = vq.getQualifier().getSourceType().getFullyQualifiedName();
				String qualifierPackage = qualifierTypeName.substring(0,qualifierTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
				String name = vq.getQualifier().getSourceType().getElementName();

				return name+" - "+qualifierPackage;
			}
			return "";
		}

		public Color getForeground(Object element) {
			if(element instanceof ValuedQualifier){
				if(contains(originalQualifiers, (ValuedQualifier)element))
					return ColorConstants.lightGray;
			}
			return ColorConstants.black;
		}

		public Color getBackground(Object element) {
			return null;
		}
	}
	
	class QualifiersListContentProvider implements IStructuredContentProvider{

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ArrayList){
				return ((ArrayList)inputElement).toArray();
			}
			return new Object[]{};
		}
		
	}
	
	public class QualifierFilter extends ViewerFilter {
		SearchPattern patternMatcher = new SearchPattern();
		public boolean isConsistentItem(Object item) {
			return true;
		}

		public boolean select(Viewer viewer, Object parentElement,
	            Object element) {
			
			if (element instanceof ValuedQualifier) {
				String qualifierTypeName = ((ValuedQualifier)element).getQualifier().getSourceType().getFullyQualifiedName();
				if(pattern.getText().isEmpty())
					patternMatcher.setPattern("*");
				else
					patternMatcher.setPattern(pattern.getText());
				boolean result = patternMatcher.matches(qualifierTypeName);
				if (!result) {
					String pattern = patternMatcher.getPattern();
					if (pattern.indexOf(".") < 0) {
						int lastIndex = qualifierTypeName.lastIndexOf(".");
						if (lastIndex >= 0
								&& (lastIndex + 1) < qualifierTypeName.length())
							return patternMatcher.matches(qualifierTypeName.substring(lastIndex + 1));
					}
				}
				return result;
			}
			return false;
		}
	}
	
	public static class ValuedQualifier{
		private IQualifier qualifier;
		private String value="";
		
		public ValuedQualifier(IQualifier qualifier){
			this.qualifier = qualifier;
		}
		
		public ValuedQualifier(IQualifier qualifier, String value){
			this(qualifier);
			this.value = value;
		}
		
		public IQualifier getQualifier(){
			return qualifier;
		}
		
		public String getValue(){
			return value;
		}
		
		public void setValue(String value){
			this.value = value;
		}

		public boolean equals(Object obj) {
			if(obj instanceof ValuedQualifier)
				return getQualifier().getSourceType().getFullyQualifiedName().equals(((ValuedQualifier)obj).getQualifier().getSourceType().getFullyQualifiedName());
			return false;
		}
	}
	
	static class ValueDialog extends MessageDialog{
		String value;
		Text text;

		public ValueDialog(Shell shell, String value) {
			super(shell, "Edit Qualifier Annotation Value", null, "",
					MessageDialog.NONE, new String[]{"Ok", "Cancel"}, 0);
			this.value = value;
		}

		protected Control createCustomArea(Composite parent) {
			Composite composite = new Composite(parent, 0);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 5;
			layout.marginWidth = 5;
			layout.horizontalSpacing = 5;
			layout.verticalSpacing = 5;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));

			Label label = new Label(composite, SWT.NONE);
			label.setText("Qualifier annotation value:");
			
			text = new Text(composite, SWT.BORDER);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			text.setLayoutData(data);
			text.setText(value);
			text.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					value = text.getText();
				}
			});

			return composite;
		}
		
		public String getValue(){
			return value;
		}
	}

}