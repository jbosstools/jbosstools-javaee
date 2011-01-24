/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import java.util.ArrayList;

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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SearchPattern;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class SelectBeanWizard extends AbstractModifyInjectionPointWizard{
	private AddQualifiersToBeanWizardPage page;
	private Text pattern;
	
	public SelectBeanWizard(IInjectionPoint injectionPoint, java.util.List<IBean> beans){
		super(injectionPoint, beans);
		setWindowTitle(CDIUIMessages.SELECT_BEAN_WIZARD_TITLE);
		
		setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
	}
	
    public void addPages() {
    	addPage(new SelectBeanWizardPage(""));
    	page = new AddQualifiersToBeanWizardPage("");
    	addPage(page);
    }
    
	public java.util.List<IQualifier> getDeployedQualifiers(){
		return page.getDeployedQualifiers();
	}
	
	public java.util.List<IQualifier> getAvailableQualifiers(){
		return page.getAvailableQualifiers();
	}
	
	public void init(IBean bean){
		page.init(bean);
	}
	
	public void deploy(IQualifier qualifier){
		page.deploy(qualifier);
	}
	
	public boolean checkBeans(){
		return page.checkBeans();
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	class SelectBeanWizardPage extends WizardPage{
		ListViewer listViewer;
		protected SelectBeanWizardPage(String pageName) {
			super(pageName);
			setTitle(CDIUIMessages.SELECT_BEAN_TITLE);
			setPageComplete(false);
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.horizontalSpacing = 4;
			layout.verticalSpacing = 10;
			layout.numColumns = 1;
			composite.setLayout(layout);
			composite.setFont(composite.getParent().getFont());
			
			Label label = new Label(composite, SWT.NONE);
			label.setText(CDIUIMessages.SELECT_BEAN_WIZARD_ENTER_BEAN_NAME);
			
			pattern = new Text(composite, SWT.BORDER);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			pattern.setLayoutData(data);
			pattern.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e){
					listViewer.refresh();
				}
			});
			
			label = new Label(composite, SWT.NONE);
			label.setText(CDIUIMessages.SELECT_BEAN_WIZARD_SELECT_BEAN);
			
			List availableList = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			data = new GridData(GridData.FILL_BOTH);
			data.heightHint = 200;
			data.widthHint = 150;
			availableList.setLayoutData(data);
			
			listViewer = new ListViewer(availableList);
			ILabelProvider labelProvider = new BeanListLabelProvider();
			listViewer.setLabelProvider(labelProvider);
			IContentProvider contentProvider = new BeanListContentProvider();
			listViewer.setContentProvider(contentProvider);
			listViewer.setComparator(new ViewerComparator() {
				public int compare(Viewer viewer, Object o1, Object o2) {
					if (o1 instanceof IBean && o2 instanceof IBean) {
						IBean b1 = (IBean) o1;
						IBean b2 = (IBean) o2;
						return (b1.getBeanClass().getElementName().compareToIgnoreCase(b2.getBeanClass().getElementName()));
					}
					
					return super.compare(viewer, o1, o2);
				}
			});
			listViewer.setInput(beans);
			
			listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					bean = getSelection();
					if(bean != null){
						setPageComplete(true);
						IWizardPage next = getNextPage();
						if(next instanceof AddQualifiersToBeanWizardPage)
							((AddQualifiersToBeanWizardPage)next).init(bean);
					}else
						setPageComplete(false);
				}
			});
			listViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
				}
			});
			listViewer.addFilter(new BeanFilter());
			
			setControl(composite);
		}
		
		protected IBean getSelection() {
			IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
			if (sel.isEmpty())
				return null;
				
			return (IBean)sel.getFirstElement();
		}
	}
	
	public class BeanFilter extends ViewerFilter {
		SearchPattern patternMatcher = new SearchPattern();
		public boolean isConsistentItem(Object item) {
			return true;
		}

		public boolean select(Viewer viewer, Object parentElement,
	            Object element) {
			
			if (element instanceof IBean) {
				String beanTypeName = ((IBean)element).getBeanClass().getFullyQualifiedName();
				if(pattern.getText().isEmpty())
					patternMatcher.setPattern("*");
				else
					patternMatcher.setPattern(pattern.getText());
				boolean result = patternMatcher.matches(beanTypeName);
				if (!result) {
					String pattern = patternMatcher.getPattern();
					if (pattern.indexOf(".") < 0) {
						int lastIndex = beanTypeName.lastIndexOf(".");
						if (lastIndex >= 0
								&& (lastIndex + 1) < beanTypeName.length())
							return patternMatcher.matches(beanTypeName.substring(lastIndex + 1));
					}
				}
				return result;
			}
			return false;
		}
	}

	
	class BeanListLabelProvider implements ILabelProvider{

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
			if(element instanceof IBean){
				IBean bean = (IBean)element;
				String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
				String beanPackage = beanTypeName.substring(0,beanTypeName.lastIndexOf(MarkerResolutionUtils.DOT));
				String name = bean.getBeanClass().getElementName();

				return name+" - "+beanPackage;
			}
			return "";
		}
		
	}
	
	class BeanListContentProvider implements IStructuredContentProvider{

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
