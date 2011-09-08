/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.java.IParametedType;

public class AssignableBeansDialog extends TitleAreaDialog {
	IInjectionPoint injectionPoint;
	Set<IBean> beans = new HashSet<IBean>();
	Set<IBean> eligibleBeans = new HashSet<IBean>();
	Set<IBean> resolvedBeans = new HashSet<IBean>();

	CheckboxTreeViewer filterView;
	TableViewer list;

	public AssignableBeansDialog(Shell parentShell) {
		super(parentShell);
	}

	public void setInjectionPoint(IInjectionPoint injectionPoint) {
		this.injectionPoint = injectionPoint;
		beans = injectionPoint.getCDIProject().getBeans(false, injectionPoint);
		eligibleBeans = new HashSet<IBean>(beans);
		for (int i = OPTION_UNAVAILABLE_BEANS + 1; i < OPTION_ELIMINATED_AMBIGUOUS; i++) {
			Filter f = ALL_OPTIONS[i].filter;
			if(f != null) {
				f.filter(eligibleBeans);
			}
		}
		resolvedBeans = injectionPoint.getCDIProject().getBeans(true, injectionPoint);
	}

	String computeTitle() {
		StringBuffer result = new StringBuffer();
		result.append("@Inject ");
		
		if(injectionPoint instanceof IInjectionPointParameter) {
			IMethod m = ((IInjectionPointParameter)injectionPoint).getBeanMethod().getMethod();
			result.append(m.getElementName()).append("(");
		}
		Set<IQualifierDeclaration> ds = injectionPoint.getQualifierDeclarations();
		for (IQualifierDeclaration d: ds) {
			result.append("@").append(d.getType().getElementName()).append(" ");
		}
		
		IParametedType type = injectionPoint.getMemberType();
		result.append(type.getType().getElementName()).append(" ");

		if(injectionPoint instanceof IInjectionPointParameter) {
			result.append(((IInjectionPointParameter)injectionPoint).getName());
			result.append(")");
		} else {
			result.append(injectionPoint.getSourceMember().getElementName());
		}

		return result.toString();
	}

	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.CLOSE_ID) {
			cancelPressed();
		}
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		setTitle(computeTitle());
		createListView(composite);
		createFilterView(composite);
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL,
				true);
	}
	
	void createListView(Composite parent) {
		list = new TableViewer(parent);
		GridData g = new GridData(GridData.FILL_BOTH);
		list.getControl().setLayoutData(g);
		list.setContentProvider(new ListContent());
		list.setLabelProvider(new LP());
		list.setInput(injectionPoint);
	}

	void createFilterView(Composite parent) {
		Group g = new Group(parent, 0);
		g.setText("Show/Hide");
		g.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_BEGINNING));
		g.setLayout(new GridLayout(1, false));
		filterView = new CheckboxTreeViewer(g);
		filterView.setAutoExpandLevel(3);
		filterView.setContentProvider(checkboxContentProvider);
		filterView.setInput(ROOT);
		for (int i = 1; i < ALL_OPTIONS.length; i++) {
			filterView.setChecked(ALL_OPTIONS[i], true);
		}
		filterView.getControl().setBackground(g.getBackground());
		g.setData(new GridData(GridData.FILL_BOTH));
		filterView.addCheckStateListener(new ICheckStateListener() {			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				((Checkbox)event.getElement()).state = event.getChecked();
				filterView.refresh();
				list.refresh();
			}
		});
		filterView.setCheckStateProvider(new ICheckStateProvider() {
			
			@Override
			public boolean isGrayed(Object element) {
				Checkbox c = (Checkbox)element;
				return c.parent != null && !c.parent.state;
			}
			
			@Override
			public boolean isChecked(Object element) {
				Checkbox c = (Checkbox)element;
				return c.state;
			}
		});
	}

	static int OPTION_UNAVAILABLE_BEANS = 1;
	static int OPTION_DECORATOR = 2;
	static int OPTION_INTERCEPTOR = 3;
	static int OPTION_UNSELECTED_ALTERNATIVE = 4;
	static int OPTION_PRODUCER_IN_UNAVAILABLE_BEAN = 5;
	static int OPTION_SPECIALIZED_BEAN = 6;
	static int OPTION_ELIMINATED_AMBIGUOUS = 7;

	static class Checkbox {
		int id;
		String label;
		Filter filter;
		boolean state = true;
		Checkbox(int id, String label, Filter filter) {
			this.id = id;
			this.label = label;
			this.filter = filter;
		}
		Checkbox parent = null;
		List<Checkbox> children = new ArrayList<Checkbox>();

		Checkbox add(int id, String label, Filter f) {
			Checkbox c = new Checkbox(id, label, f);
			ALL_OPTIONS[id] = c;
			c.parent = this;
			children.add(c);			
			return c;
		}
	
		public String toString() {
			return label;
		}
	
		public boolean isEnabled() {
			return state && (parent == null || parent.isEnabled());
		}
	
		public void filter(Set<IBean> beans) {
			if(id == OPTION_ELIMINATED_AMBIGUOUS) filter = new EliminatedAmbiguousFilter();
			if(!isEnabled() && filter != null) {
				filter.filter(beans);
			}
			for (Checkbox c: children) {
				c.filter(beans);
			}
		}
	}

	static Checkbox[] ALL_OPTIONS = new Checkbox[8];

	static Checkbox ROOT = new Checkbox(0, "", null);
	
	static {
		Checkbox unavailable = ROOT.add(OPTION_UNAVAILABLE_BEANS, "Unavailable Beans", null);
		unavailable.add(OPTION_DECORATOR, "@Decorator", new DecoratorFilter());
		unavailable.add(OPTION_INTERCEPTOR, "@Interceptor", new InterceptorFilter());
		unavailable.add(OPTION_UNSELECTED_ALTERNATIVE, "Unselected @Alternative", new UnselectedAlternativeFilter());
		unavailable.add(OPTION_PRODUCER_IN_UNAVAILABLE_BEAN, "@Produces in unavailable bean", new ProducerFilter());
		unavailable.add(OPTION_SPECIALIZED_BEAN, "Specialized beans", new SpecializedBeanFilter());
		ROOT.add(OPTION_ELIMINATED_AMBIGUOUS, "Eliminated ambiguous", new EliminatedAmbiguousFilter());
	}
	
	static CheckboxContentProvider checkboxContentProvider = new CheckboxContentProvider();
	
	static class CheckboxContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((Checkbox)parentElement).children.toArray(new Checkbox[0]);
		}

		@Override
		public Object getParent(Object element) {
			return ((Checkbox)element).parent;
		}

		@Override
		public boolean hasChildren(Object element) {
			return !((Checkbox)element).children.isEmpty();
		}
	}
	
	class ListContent implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			Set<IBean> bs = new HashSet<IBean>(beans);
			LP p = new LP();
			ROOT.filter(bs);
			Map<String, IBean> map = new TreeMap<String, IBean>();
			for (IBean b: bs) {
				if(resolvedBeans.contains(b)) {
					map.put(p.getText(b), b);
				}
			}
			List<IBean> sorted = new ArrayList<IBean>();
			sorted.addAll(map.values());
			bs.removeAll(map.values());
			map.clear();

			for (IBean b: bs) {
				if(eligibleBeans.contains(b)) {
					map.put(p.getText(b), b);
				}
			}
			sorted.addAll(map.values());
			bs.removeAll(map.values());
			map.clear();

			for (IBean b: bs) {
				map.put(p.getText(b), b);
			}
			sorted.addAll(map.values());

			return sorted.toArray(new IBean[0]);
		}
		
	}

	class LP extends LabelProvider implements ITableLabelProvider, IFontProvider, IColorProvider {
		public String getText(Object element) {
			IBean b = (IBean)element;
			StringBuffer sb = new StringBuffer();
			if(b.isAlternative()) {
				sb.append("@Alternative ");
			}
			if(b.isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)) {
				sb.append("@Decorator ");
			}
			if(b.isAnnotationPresent(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)) {
				sb.append("@Interceptor ");
			}
			if(b instanceof IProducer) {
				sb.append("@Produces ");
				sb.append(b.getBeanClass().getElementName()).append(".");
				if(b instanceof IProducerField) {
					sb.append(((IProducerField)b).getField().getElementName());
				} else {
					sb.append(((IProducerMethod)b).getMethod().getElementName());
				}
			} else {
				sb.append(b.getBeanClass().getElementName());
			}
			String pkg = b.getBeanClass().getPackageFragment().getElementName();
			sb.append(" - ").append(pkg).append(" - ");
			return sb.toString();
		}

		@Override
		public Font getFont(Object element) {
			IBean b = (IBean)element;
			if(eligibleBeans.contains(b)) {
				return JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
			}
			return JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
		}
		
		Color gray = new Color(null, 128, 128, 128);

		@Override
		public Color getForeground(Object element) {
			IBean b = (IBean)element;
			if(!eligibleBeans.contains(b)) {
				return gray;
			}
			return null;
		}

		@Override
		public Color getBackground(Object element) {
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return getText(element);
		}		
	}

	static class Filter {
		public void filter(Set<IBean> beans) {
		}
	}

	static class DecoratorFilter extends Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b.isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)) {
					it.remove();
				}
			}
		}
	}

	static class InterceptorFilter extends Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b.isAnnotationPresent(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)) {
					it.remove();
				}
			}
		}
	}

	static class UnselectedAlternativeFilter extends Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b.isAlternative() && !b.isSelectedAlternative()) {
					it.remove();
				}
			}
		}
	}

	static class SpecializedBeanFilter extends Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b instanceof IClassBean && !((IClassBean) b).getSpecializingBeans().isEmpty()) {
					it.remove();
				}
			}
		}
	}

	static class ProducerFilter extends Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b instanceof IProducer && ((IProducer) b).getClassBean() != null && !((IProducer) b).getClassBean().isEnabled()) {
					it.remove();
				}
			}
		}
	}

	static class EliminatedAmbiguousFilter extends Filter {
		public void filter(Set<IBean> beans) {
			Set<IBean> eligible = new HashSet<IBean>(beans);
			for (int i = OPTION_UNAVAILABLE_BEANS + 1; i < OPTION_ELIMINATED_AMBIGUOUS; i++) {
				Filter f = ALL_OPTIONS[i].filter;
				if(f != null) {
					f.filter(eligible);
				}
			}
			boolean hasAlternative = false;
			for (IBean b: eligible) {
				if(b.isEnabled() && b.isAlternative() && b.isSelectedAlternative()) {
					hasAlternative = true;
				}
			}
			if(hasAlternative) {
				Iterator<IBean> it = beans.iterator();
				while(it.hasNext()) {
					IBean bean = it.next();
					if(!eligible.contains(bean) || bean.isAlternative()) continue;
					if(bean instanceof IProducer && bean instanceof IBeanMember) {
						IBeanMember p = (IBeanMember)bean;
						if(p.getClassBean() != null && p.getClassBean().isAlternative()) continue;
					}
					it.remove();
				}
			}
		}
		
	}

}
