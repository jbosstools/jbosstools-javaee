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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters.Checkbox;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters.Filter;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AssignableBeansDialog extends PopupDialog {// TitleAreaDialog {
	IInjectionPoint injectionPoint;
	Set<IBean> beans = new HashSet<IBean>();
	Set<IBean> eligibleBeans = new HashSet<IBean>();
	Set<IBean> resolvedBeans = new HashSet<IBean>();

	Composite composite;

	boolean showHideOptions = true;
	Group filterPanel;
	CheckboxTreeViewer filterView;

	TableViewer list;

	public AssignableBeansDialog(Shell parentShell) {
		// TitleAreaDialog
//		super(parentShell);
		//PopupDialog
		super(parentShell, 0, true, true, true, true, true, "title", null);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		initSettings();
	}

	protected void initSettings() {
		IDialogSettings settings = getDialogSettings();
		showHideOptions = settings.getBoolean(SHOW_HIDE_PANEL);
		for (Checkbox c: AssignableBeanFilters.ALL_OPTIONS) {
			c.state = settings.getBoolean(FILTER_OPTION + c.id);
		}
		AssignableBeanFilters.ROOT.state = true;
	}

	public void setInjectionPoint(IInjectionPoint injectionPoint) {
		this.injectionPoint = injectionPoint;
		beans = injectionPoint.getCDIProject().getBeans(false, injectionPoint);
		eligibleBeans = new HashSet<IBean>(beans);
		for (int i = AssignableBeanFilters.OPTION_UNAVAILABLE_BEANS + 1; i < AssignableBeanFilters.OPTION_ELIMINATED_AMBIGUOUS; i++) {
			Filter f = AssignableBeanFilters.ALL_OPTIONS[i].filter;
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
			// TitleAreaDialog
//			cancelPressed();
		}
	}

	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		// TitleAreaDialog
//		setTitle(computeTitle());
		// PopupDialog
		setTitleText(computeTitle());
		createListView(composite);
		if(showHideOptions) {
			createFilterView(composite);
		}
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// TitleAreaDialog
//		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	void createListView(Composite parent) {
		list = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER/* | SWT.VIRTUAL*/);
		GridData g = new GridData(GridData.FILL_BOTH);
		list.getControl().setLayoutData(g);
		list.setContentProvider(new ListContent());
		list.setLabelProvider(new LP());
		list.setInput(injectionPoint);
		list.addOpenListener(new IOpenListener() {			
			@Override
			public void open(OpenEvent event) {
				ISelection s = event.getSelection();
				if(!s.isEmpty() && s instanceof IStructuredSelection) {
					Object o = ((IStructuredSelection)s).getFirstElement();
					if(o instanceof IBean) {
						((IBean)o).open();
						close();
					}
				}
			}
		});

		// PopupDialog
		list.getTable().addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				ViewerCell cell = list.getCell(new Point(e.x, e.y));
				if(cell != null) {
					Widget w = cell.getItem();
					if(w != null && w.getData() != null) {
						list.setSelection(new StructuredSelection(w.getData()));
					}
				}
				list.getTable().setCursor(cell == null ? null : list.getTable().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
			}
		});
		list.getTable().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				ViewerCell cell = list.getCell(new Point(e.x, e.y));
				if(cell != null) {
					Widget w = cell.getItem();
					if(w != null && w.getData() instanceof IBean) {
						IBean bean = (IBean)w.getData();
						bean.open();
						close();
					}
				}
			}
		});

		list.refresh();
	}

	void createFilterView(Composite parent) {
		Group g = filterPanel = new Group(parent, 0);
		g.setBackground(parent.getBackground());
		g.setText("Show/Hide");
		g.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_BEGINNING));
		g.setLayout(new GridLayout(1, false));
		filterView = new CheckboxTreeViewer(g, 0);
		filterView.setAutoExpandLevel(3);
		filterView.setContentProvider(checkboxContentProvider);
		filterView.setInput(AssignableBeanFilters.ROOT);
		for (int i = 1; i < AssignableBeanFilters.ALL_OPTIONS.length; i++) {
			filterView.setChecked(AssignableBeanFilters.ALL_OPTIONS[i], true);
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

	protected String getId() {
		return AssignableBeansDialog.class.getName();
	}

	protected Point getDefaultSize() {
		return new Point(700, 400);
	}

	protected Point getDefaultLocation(Point size) {
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		Rectangle b = display.getActiveShell().getBounds();
		int x = b.x + (b.width - size.x) / 2;
		int y = b.y + (b.height - size.y) / 2;
		return new Point(x, y);
	}

	// PopupDialog
	protected void fillDialogMenu(IMenuManager dialogMenu) {
		super.fillDialogMenu(dialogMenu);
		dialogMenu.add(new ShowHideAction());
	}
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = CDIExtensionsPlugin.getDefault().getDialogSettings().getSection(getId());
		if(settings == null) {
			settings = CDIExtensionsPlugin.getDefault().getDialogSettings().addNewSection(getId());
			settings.put(SHOW_HIDE_PANEL, true);
			for (Checkbox c: AssignableBeanFilters.ALL_OPTIONS) {
				settings.put(FILTER_OPTION + c.id, c.state);
			}
		}
		return settings;
	}

	public boolean close() {
		saveFilterOptions();
		return super.close();
	}

	private static final String SHOW_HIDE_PANEL = "SHOW_HIDE_PANEL";
	private static final String FILTER_OPTION = "FILTER_OPTION_";

	private void saveFilterOptions() {
		IDialogSettings settings = getDialogSettings();
		settings.put(SHOW_HIDE_PANEL, showHideOptions);
		for (Checkbox c: AssignableBeanFilters.ALL_OPTIONS) {
			settings.put(FILTER_OPTION + c.id, c.state);
		}
	}


	class ShowHideAction extends Action {
		public ShowHideAction() {
			super("Show/Hide panel", Action.AS_CHECK_BOX);
			setChecked(showHideOptions);
		}
		public void run() {
			setFiltersEnabled(isChecked());
		}
	}

	protected void setFiltersEnabled(boolean enabled) {
		if(enabled != showHideOptions) {
			showHideOptions = enabled;
			if(!enabled && filterPanel != null) {
				filterPanel.dispose();
				filterPanel = null;
				filterView = null;
			} else if(enabled && filterPanel == null) {
				createFilterView(composite);
			}
			composite.update();
			composite.layout(true);
		}
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
			Set<String> keys = new HashSet<String>();
			LP p = new LP();
			AssignableBeanFilters.ROOT.filter(bs);
			Map<String, IBean> map = new TreeMap<String, IBean>();
			for (IBean b: bs) {
				if(resolvedBeans.contains(b)) {
					String key = p.getText(b);
					if(!keys.contains(key)) {
						map.put(key, b);
						keys.add(key);
					}
				}
			}
			List<IBean> sorted = new ArrayList<IBean>();
			sorted.addAll(map.values());
			bs.removeAll(map.values());
			map.clear();

			for (IBean b: bs) {
				if(eligibleBeans.contains(b)) {
					String key = p.getText(b);
					if(!keys.contains(key)) {
						map.put(key, b);
						keys.add(key);
					}
				}
			}
			sorted.addAll(map.values());
			bs.removeAll(map.values());
			map.clear();

			for (IBean b: bs) {
				String key = p.getText(b);
				if(!keys.contains(key)) {
					map.put(key, b);
					keys.add(key);
				}
			}
			sorted.addAll(map.values());

			return sorted.toArray(new IBean[0]);
		}
		
	}

	static Color gray = new Color(null, 128, 128, 128);
	static Color black = new Color(null, 0, 0, 0);

	static Styler ELIGIBLE_NAME = new DefaultStyler(black, false);
	static Styler ELIGIBLE_QUALIFIER = new DefaultStyler(gray, false);
	static Styler DISABLED = new DefaultStyler(gray, false);

	private static class DefaultStyler extends Styler {
		private final Color foreground;
		private final boolean italic;

		public DefaultStyler(Color foreground, boolean italic) {
			this.foreground = foreground;
			this.italic = italic;
		}

		public void applyStyles(TextStyle textStyle) {
			if (foreground != null) {
				textStyle.foreground = foreground;
			}
			if(italic) {
				textStyle.font = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
			}
		}
	}

	class LP extends StyledCellLabelProvider implements DelegatingStyledCellLabelProvider.IStyledLabelProvider {
		public void update(ViewerCell cell) {
			ELIGIBLE_QUALIFIER = DISABLED;
			Object element = cell.getElement();
			StyledString styledString = getStyledText(element);
			cell.setText(styledString.getString());
			cell.setStyleRanges(styledString.getStyleRanges());
			cell.setImage(getImage(element));

			super.update(cell);
		}

		public String getText(Object element) {
			return getStyledText(element).getString();
		}
		public StyledString getStyledText(Object element) {
			IBean b = (IBean)element;
			Styler nameStyler = eligibleBeans.contains(b) ? ELIGIBLE_NAME : DISABLED;
			StyledString sb = new StyledString();
			if(b.isAlternative()) {
				sb.append("@Alternative ", nameStyler);
			}
			if(b.isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)) {
				sb.append("@Decorator ", nameStyler);
			}
			if(b.isAnnotationPresent(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)) {
				sb.append("@Interceptor ", nameStyler);
			}
			if(b instanceof IProducer) {
				sb.append("@Produces ", nameStyler);
				sb.append(b.getBeanClass().getElementName(), nameStyler).append(".", nameStyler);
				if(b instanceof IProducerField) {
					sb.append(((IProducerField)b).getField().getElementName(), nameStyler);
				} else {
					sb.append(((IProducerMethod)b).getMethod().getElementName(), nameStyler)
					.append("()", nameStyler);
				}
			} else {
				sb.append(b.getBeanClass().getElementName(), nameStyler);
			}

			Styler qualifierStyler = eligibleBeans.contains(b) ? ELIGIBLE_QUALIFIER : DISABLED;
			
			AbstractBeanElement e = (AbstractBeanElement)b;
			ITextSourceReference origin = e.getDefinition().getOriginalDefinition();
			if(origin != null) {
				//If toString() is not enough, another interface should be introduced.
				sb.append(" - ", qualifierStyler).append(origin.toString(), qualifierStyler);				
			} else {			
				String pkg = b.getBeanClass().getPackageFragment().getElementName();
				sb.append(" - ", qualifierStyler).append(pkg, qualifierStyler).append(" - ", qualifierStyler);
				IPath path = b.getBeanClass().getPackageFragment().getParent().getPath();
				if(path != null) {
					sb.append(path.toString(), qualifierStyler);
				}
			}
			return sb;
		}

		public Image getImage(Object element) {
			//TODO
			return null;
		}		
	}

}
