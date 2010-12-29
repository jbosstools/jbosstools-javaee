package org.jboss.tools.cdi.ui.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;

public class AddQualifiersToBeanComposite extends Composite {
	private static Font font;
	private IInjectionPoint injectionPoint;
	private IBean bean;
	private java.util.List<IBean> beans;
	private WizardPage wizard;

	// original qualifiers on the bean
	private ArrayList<IQualifier> originalQualifiers = new ArrayList<IQualifier>();

	// qualifiers available to be added to the bean
	private ArrayList<IQualifier> qualifiers = new ArrayList<IQualifier>();

	// current qualifiers on the bean
	private ArrayList<IQualifier> deployed = new ArrayList<IQualifier>();
	
	// original + deployed
	ArrayList<IQualifier> total = new ArrayList<IQualifier>();

	private ListViewer availableListViewer;
	private ListViewer deployedListViewer;

	private Button add, addAll;
	private Button remove, removeAll;
	
	private Label nLabel;

	protected boolean isComplete = true;

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
		deployedListViewer.setInput(originalQualifiers);
		
		qualifiers.clear();
		
		loadAvailableQualifiers();
		if(nLabel != null)
			nLabel.setText(MessageFormat.format(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE,
					new Object[]{bean.getBeanClass().getElementName()}));
		refresh();
	}
	
	private void loadAvailableQualifiers(){
		String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
		String beanPackage = beanTypeName.substring(0,beanTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
		
		String injectionPointTypeName = injectionPoint.getClassBean().getBeanClass().getFullyQualifiedName();
		String injectionPointPackage = injectionPointTypeName.substring(0,injectionPointTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
		
		boolean samePackage = beanPackage.equals(injectionPointPackage);
		
		IQualifier[] qs = bean.getCDIProject().getQualifiers();
		qualifiers = new ArrayList<IQualifier>();
		
		for(IQualifier q : qs){
			if(!originalQualifiers.contains(q)){
				boolean isPublic = true;
				try{
					isPublic = Flags.isPublic(q.getSourceType().getFlags());
				}catch(JavaModelException ex){
					CDIUIPlugin.getDefault().logError(ex);
				}
				String qualifierTypeName = q.getSourceType().getFullyQualifiedName();
				String qualifierPackage = qualifierTypeName.substring(0,qualifierTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
				if(isPublic || (samePackage && injectionPointPackage.equals(qualifierPackage)))
					qualifiers.add(q);
			}
		}
		availableListViewer.setInput(qualifiers);
		
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
	
	private boolean checkBeans(){
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
		label.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_AVAILABLE);
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		label = new Label(this, SWT.NONE);
		label.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_IN_BEAN);
		
		List availableList = new List(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.widthHint = 150;
		availableList.setLayoutData(data);
		
		availableListViewer = new ListViewer(availableList);
		ILabelProvider labelProvider = new QualifiersListLabelProvider();
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
		
		List deployedList = new List(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 150;
		deployedList.setLayoutData(data);
		
		deployedListViewer = new ListViewer(deployedList);
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
		
		final Button createQualifier = new Button(this, SWT.PUSH);
		createQualifier.setText(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_CREATE_NEW_QUALIFIER);
		
		createQualifier.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
				NewQualifierCreationWizard wizard = new NewQualifierCreationWizard();
				StructuredSelection selection = new StructuredSelection(new Object[]{bean.getBeanClass()});
				
				wizard.init(Workbench.getInstance(), selection);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				int status = dialog.open();
				if(status == WizardDialog.OK){
					// reload qualifiers
					try {
						Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
					} catch (InterruptedException e) {
						// do nothing
					}
					
					qualifiers.clear();
					
					loadAvailableQualifiers();
					
					refresh();
				}
			}
		});
		
		setEnablement();
		availableList.setFocus();
		
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
					if (qualifiers.contains(qualifier)) {
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
				if (qualifier != null && deployed.contains(qualifier)) {
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
			if (!list.contains(mods[i]))
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

		setEnablement();
	}


	public ArrayList<IQualifier> getQualifiersToRemove() {
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		Iterator iterator = originalQualifiers.iterator();
		while (iterator.hasNext()) {
			IQualifier qualifier = (IQualifier) iterator.next();
			if (!deployed.contains(qualifier))
				list.add(qualifier);
		}
		return list;
	}

	public ArrayList<IQualifier> getQualifiersToAdd() {
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		Iterator iterator = deployed.iterator();
		while (iterator.hasNext()) {
			IQualifier qualifier = (IQualifier) iterator.next();
			if (!originalQualifiers.contains(qualifier))
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

	class QualifiersListLabelProvider implements ILabelProvider{

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
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
}