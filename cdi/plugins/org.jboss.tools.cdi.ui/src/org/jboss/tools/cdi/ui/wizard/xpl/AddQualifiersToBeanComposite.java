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
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SearchPattern;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;
import org.jboss.tools.cdi.ui.wizard.AbstractModifyInjectionPointWizard;
import org.jboss.tools.cdi.ui.wizard.NewQualifierCreationWizard;

public class AddQualifiersToBeanComposite extends Composite {
	private static Font font;
	private IInjectionPoint injectionPoint;
	private IBean bean;
	private java.util.List<IBean> beans;
	private WizardPage wizard;
	private Text pattern;

	// original qualifiers on the bean
	private ArrayList<IQualifier> originalQualifiers = new ArrayList<IQualifier>();

	// qualifiers available to be added to the bean
	private ArrayList<IQualifier> qualifiers = new ArrayList<IQualifier>();

	// current qualifiers on the bean
	private ArrayList<IQualifier> deployed = new ArrayList<IQualifier>();
	
	// original + deployed
	ArrayList<IQualifier> total = new ArrayList<IQualifier>();

	private TableViewer availableListViewer;
	private TableViewer deployedListViewer;

	private Button add, addAll;
	private Button remove, removeAll;
	
	private Label nLabel;
	
	protected boolean isComplete = true;
	
	private boolean hasDefaultQualifier = false;
	
	private IQualifier defaultQualifier, namedQualifier;
	
	private ILabelProvider labelProvider = new QualifiersListLabelProvider();

	public AddQualifiersToBeanComposite(Composite parent, WizardPage wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.injectionPoint = ((AbstractModifyInjectionPointWizard)wizard.getWizard()).getInjectionPoint();
		this.bean = ((AbstractModifyInjectionPointWizard)wizard.getWizard()).getBean();
		this.beans = ((AbstractModifyInjectionPointWizard)wizard.getWizard()).getBeans();
		
		createControl();
		if(bean != null)
			init(bean);
	}
	
	public void init(IBean bean){
		this.bean = bean;
		originalQualifiers = new ArrayList<IQualifier>(bean.getQualifiers());
		
		defaultQualifier = bean.getCDIProject().getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		namedQualifier = bean.getCDIProject().getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		
		for(IQualifier q : originalQualifiers){
			if(q.equals(defaultQualifier)){
				hasDefaultQualifier = true;
				break;
			}
		}
		
		deployedListViewer.setInput(originalQualifiers);
		
		qualifiers.clear();
		
		loadAvailableQualifiers();
		availableListViewer.setInput(qualifiers);
		if(nLabel != null)
			nLabel.setText(MessageFormat.format(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE,
					new Object[]{bean.getBeanClass().getElementName()}));
		refresh();
	}
	
	private IQualifier loadAvailableQualifiers(){
		IQualifier lastQualifier = null;
		String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
		String beanPackage = beanTypeName.substring(0,beanTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
		
		String injectionPointTypeName = injectionPoint.getClassBean().getBeanClass().getFullyQualifiedName();
		String injectionPointPackage = injectionPointTypeName.substring(0,injectionPointTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
		
		boolean samePackage = beanPackage.equals(injectionPointPackage);
		
		IQualifier[] qs = bean.getCDIProject().getQualifiers();
		
		for(IQualifier q : qs){
			if(!contains(originalQualifiers, q) && !contains(qualifiers, q) && !contains(deployed, q)){
				boolean isPublic = true;
				try{
					isPublic = Flags.isPublic(q.getSourceType().getFlags());
				}catch(JavaModelException ex){
					CDIUIPlugin.getDefault().logError(ex);
				}
				String qualifierTypeName = q.getSourceType().getFullyQualifiedName();
				String qualifierPackage = qualifierTypeName.substring(0,qualifierTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
				if((isPublic || (samePackage && injectionPointPackage.equals(qualifierPackage))) ){
					qualifiers.add(q);
					lastQualifier = q;
				}
			}
		}
		return lastQualifier;
	}
	
	private boolean contains(ArrayList<IQualifier> qualifiers, IQualifier qualifier){
		String qualifierText = labelProvider.getText(qualifier);
		for(IQualifier q : qualifiers){
			String qText = labelProvider.getText(q);
			if(qText.equals(qualifierText))
				return true;
		}
		return false;
	}

	public void setVisible(boolean visible) {
		if (visible)
			this.refresh();
		super.setVisible(visible);
	}

	public void refresh() {

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (availableListViewer == null || availableListViewer.getControl().isDisposed())
					return;
				availableListViewer.refresh();
				deployedListViewer.refresh();
				setEnablement();
			}
		});
	}
	
	public boolean checkBeans(){
		total.clear();
		total.addAll(originalQualifiers);
		total.addAll(deployed);
		HashSet<IQualifier> qfs = new HashSet<IQualifier>(total);
		
		for(IBean b: beans){
			if(b.equals(bean))
				continue;
			if(checkBeanQualifiers(b, qfs))
				return false;
				
		}
		return true;
	}
	
	public static boolean checkBeanQualifiers(IBean bean, Set<IQualifier> qualifiers){
		for(IQualifier qualifier : qualifiers){
			if(!isBeanContainQualifier(bean.getQualifiers(), qualifier)){
				return false;
			}
		}
		if(bean.getQualifiers().size() == qualifiers.size())
			return true;
		return false;
	}
	
	public static boolean isBeanContainQualifier(Set<IQualifier> qualifiers, IQualifier qualifier){
		for(IQualifier q : qualifiers){
			if(q.getSourceType().getFullyQualifiedName().equals(qualifier.getSourceType().getFullyQualifiedName()))
				return true;
		}
		return false;
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
				new Object[]{bean.getBeanClass().getElementName()}));
		
		Label label = new Label(this, SWT.NONE);
		label.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_ENTER_QUALIFIER_NAME);
		label.setLayoutData(data);
		
		pattern = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		pattern.setLayoutData(data);
		pattern.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				availableListViewer.refresh();
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
		
		Table availableList = new Table(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.widthHint = 150;
		availableList.setLayoutData(data);
		
		availableListViewer = new TableViewer(availableList);
		
		availableListViewer.setLabelProvider(labelProvider);
		IContentProvider contentProvider = new QualifiersListContentProvider();
		availableListViewer.setContentProvider(contentProvider);
		availableListViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object o1, Object o2) {
				if (o1 instanceof IQualifier && o2 instanceof IQualifier) {
					IQualifier q1 = (IQualifier) o1;
					IQualifier q2 = (IQualifier) o2;
					return (q1.getSourceType().getElementName().compareToIgnoreCase(q2.getSourceType().getElementName()));
				}
				
				return super.compare(viewer, o1, o2);
			}
		});
		availableListViewer.setInput(qualifiers);
		
		availableListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setEnablement();
			}
		});
		availableListViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				setEnablement();
				if (add.isEnabled())
					add(false);
			}
		});
		availableListViewer.addFilter(new QualifierFilter());
		
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
		
		Table deployedList = new Table(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 150;
		deployedList.setLayoutData(data);
		
		deployedListViewer = new TableViewer(deployedList);
		deployedListViewer.setLabelProvider(labelProvider);
		deployedListViewer.setContentProvider(contentProvider);
		deployedListViewer.setComparator(new ViewerComparator() {
		public int compare(Viewer viewer, Object o1, Object o2) {
			if (o1 instanceof IQualifier && o2 instanceof IQualifier) {
				IQualifier q1 = (IQualifier) o1;
				IQualifier q2 = (IQualifier) o2;
				return (q1.getSourceType().getElementName().compareToIgnoreCase(q2.getSourceType().getElementName()));
			}
			
			return super.compare(viewer, o1, o2);
			}
		});
		deployedListViewer.setInput(originalQualifiers);
		
		deployedListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setEnablement();
			}
		});
		deployedListViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				setEnablement();
				if (remove.isEnabled())
					remove(false);
			}
		});
		
		label = new Label(this, SWT.NONE);
		label.setText("");

		label = new Label(this, SWT.NONE);
		label.setText("");
		
		final Button createQualifier = new Button(this, SWT.PUSH);
		createQualifier.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_CREATE_NEW_QUALIFIER);
		
		createQualifier.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				NewQualifierCreationWizard wizard = new NewQualifierCreationWizard();
				StructuredSelection selection = new StructuredSelection(new Object[]{bean.getBeanClass()});
				
				wizard.init(PlatformUI.getWorkbench(), selection);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				int status = dialog.open();
				if(status == WizardDialog.OK){
					// reload qualifiers
					if (Display.getCurrent() != null) {
						try{
							PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress(){
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									monitor.beginTask(CDICoreMessages.CDI_UTIL_BUILD_CDI_MODEL, 10);
									monitor.worked(3);
									
									try {
										Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
									} catch (InterruptedException e) {
										// do nothing
									}
									
									monitor.worked(7);
								}
							});
						}catch(InterruptedException ie){
							CDICorePlugin.getDefault().logError(ie);
						}catch(InvocationTargetException ite){
							CDICorePlugin.getDefault().logError(ite);
						}
					}
					
					IQualifier q = loadAvailableQualifiers();
					
					if(q != null){
						moveAll(new IQualifier[]{q}, true);
					}
				}
			}
		});
		
		setEnablement();
		
		Dialog.applyDialogFont(this);
	}

	protected IQualifier[] getAvailableSelection() {
		IStructuredSelection sel = (IStructuredSelection) availableListViewer.getSelection();
		if (sel.isEmpty())
			return new IQualifier[0];
			
		IQualifier[]  mss = new IQualifier[sel.size()];
		System.arraycopy(sel.toArray(), 0, mss, 0, sel.size());
		return mss;
	}

	protected IQualifier[] getDeployedSelection() {
		IStructuredSelection sel = (IStructuredSelection) deployedListViewer.getSelection();
		if (sel.isEmpty())
			return new IQualifier[0];
		
		IQualifier[]  mss = new IQualifier[sel.size()];
		System.arraycopy(sel.toArray(), 0, mss, 0, sel.size());
		return mss;
	}

	protected void setEnablement() {
		isComplete = true;
		
		IQualifier[] ms = getAvailableSelection();
		if (ms == null ||  ms.length == 0) {
			add.setEnabled(false);
		} else {
			boolean enabled = false;
			for (int i = 0; i < ms.length; i++) {
				IQualifier qualifier = ms[i];
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
		} else {
			boolean enabled = false;
			for (int i = 0; i < ms.length; i++) {
				IQualifier qualifier = ms[i];
				if (qualifier != null && contains(deployed, qualifier)) {
					enabled = true;
				}
				else{
					enabled = false;
					break;
				}
			}
			remove.setEnabled(enabled);
		}
	
		removeAll.setEnabled(deployed.size() > 0);
		
		// check uniqueness of qualifiers
		isComplete = checkBeans();
		if(isComplete)
			wizard.setMessage("");
		else
			wizard.setMessage(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_SET_IS_NOT_UNIQUE, IMessageProvider.ERROR);
		
		wizard.setPageComplete(isComplete);
	}

	protected void add(boolean all) {
		if (all) {
			IQualifier[] qualifiers2 = new IQualifier[qualifiers.size()];
			qualifiers.toArray(qualifiers2);
			moveAll(qualifiers2, true);
		} else
			moveAll(getAvailableSelection(), true);
	}

	protected void remove(boolean all) {
		if (all) {
			ArrayList<IQualifier> list = new ArrayList<IQualifier>();
			list.addAll(deployed);
			
			IQualifier[] qualifiers2 = new IQualifier[list.size()];
			list.toArray(qualifiers2);
			
			moveAll(qualifiers2, false);
		} else
			moveAll(getDeployedSelection(), false);
	}

	protected void moveAll(IQualifier[] mods, boolean add2) {
		int size = mods.length;
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		for (int i = 0; i < size; i++) {
			if (!contains(list, mods[i]))
				list.add(mods[i]);
		}
		
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IQualifier qualifier = (IQualifier) iterator.next();
			if (add2) {
				qualifiers.remove(qualifier);
				deployed.add(qualifier);
				availableListViewer.remove(qualifier);
				deployedListViewer.add(qualifier);
			} else {
				qualifiers.add(qualifier);
				deployed.remove(qualifier);
				availableListViewer.add(qualifier);
				deployedListViewer.remove(qualifier);
			}
		}
		
		if(hasDefaultQualifier){
			if(deployed.isEmpty() || (namedQualifier != null && deployed.size() == 1 && deployed.contains(namedQualifier))) {
				if(!originalQualifiers.contains(defaultQualifier)){
					originalQualifiers.add(defaultQualifier);
					deployedListViewer.add(defaultQualifier);
				}
			}else{
				if(originalQualifiers.contains(defaultQualifier)){
					originalQualifiers.remove(defaultQualifier);
					deployedListViewer.remove(defaultQualifier);
				}
			}
		}

		setEnablement();
	}


	public ArrayList<IQualifier> getQualifiersToRemove() {
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		Iterator iterator = originalQualifiers.iterator();
		while (iterator.hasNext()) {
			IQualifier qualifier = (IQualifier) iterator.next();
			if (!contains(deployed, qualifier))
				list.add(qualifier);
		}
		return list;
	}

	public ArrayList<IQualifier> getQualifiersToAdd() {
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		Iterator iterator = deployed.iterator();
		while (iterator.hasNext()) {
			IQualifier qualifier = (IQualifier) iterator.next();
			if (!contains(originalQualifiers, qualifier))
				list.add(qualifier);
		}
		return list;
	}

	public boolean isComplete() {
		return isComplete;
	}
	
	public ArrayList<IQualifier> getDeployedQualifiers(){
		total.clear();
		total.addAll(originalQualifiers);
		total.addAll(deployed);

		return total;
	}
	
	public void deploy(IQualifier qualifier){
		IQualifier[] qualifiers = new IQualifier[]{qualifier};
		moveAll(qualifiers, true);
	}

	public ArrayList<IQualifier> getAvailableQualifiers(){
		return qualifiers;
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
			return null;
		}

		public String getText(Object element) {
			if(element instanceof IQualifier){
				IQualifier qualifier = (IQualifier)element;
				String qualifierTypeName = qualifier.getSourceType().getFullyQualifiedName();
				String qualifierPackage = qualifierTypeName.substring(0,qualifierTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
				String name = qualifier.getSourceType().getElementName();

				return name+" - "+qualifierPackage;
			}
			return "";
		}

		public Color getForeground(Object element) {
			if(element instanceof IQualifier){
				if(contains(originalQualifiers, (IQualifier)element))
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
			
			if (element instanceof IQualifier) {
				String qualifierTypeName = ((IQualifier)element).getSourceType().getFullyQualifiedName();
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

}