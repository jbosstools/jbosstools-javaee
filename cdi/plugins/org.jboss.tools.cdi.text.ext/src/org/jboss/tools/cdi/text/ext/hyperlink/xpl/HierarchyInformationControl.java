/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat, Inc.
 *******************************************************************************/
package org.jboss.tools.cdi.text.ext.hyperlink.xpl;

import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.ui.keys.KeySequence;
import org.eclipse.ui.keys.SWTKeySupport;
import org.jboss.tools.cdi.text.ext.hyperlink.IInformationItem;

/**
 * Show hierarchy in light-weight control.
 *
 * @since 3.0
 */
public class HierarchyInformationControl extends AbstractInformationControl {
	private IHyperlink[] hyperlinks;

	private BeanTableLabelProvider fLabelProvider;
	private KeyAdapter fKeyAdapter;

	private IHyperlink fFocus; // bean to filter for or null if type hierarchy

	public HierarchyInformationControl(Shell parent, String title, int shellStyle, int tableStyle, IHyperlink[] hyperlinks) {
		super(parent, shellStyle, tableStyle, IJavaEditorActionDefinitionIds.OPEN_HIERARCHY, true);
		this.hyperlinks = hyperlinks;
		setTitleText(title);
	}

	private KeyAdapter getKeyAdapter() {
		if (fKeyAdapter == null) {
			fKeyAdapter= new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					KeySequence[] sequences= getInvokingCommandKeySequences();
					if (sequences == null)
						return;

					for (int i= 0; i < sequences.length; i++) {
						if (sequences[i].equals(keySequence)) {
							e.doit= false;
							toggleHierarchy();
							return;
						}
					}
				}
			};
		}
		return fKeyAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasHeader() {
		return true;
	}

	@Override
	protected Text createFilterText(Composite parent) {
		// text set later
		Text text= super.createFilterText(parent);
		text.addKeyListener(getKeyAdapter());
		return text;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.JavaOutlineInformationControl#createTableViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected TableViewer createTableViewer(Composite parent, int style) {
		Table table = new Table(parent, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= table.getItemHeight() * 12;
		table.setLayoutData(gd);

		TableViewer tableViewer= new TableViewer(table);
		
		tableViewer.addFilter(new BeanFilter());

		fLabelProvider= new BeanTableLabelProvider();
		fLabelProvider.setFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return hasFocusBean((IHyperlink) element);
			}
		});

		tableViewer.setLabelProvider(fLabelProvider);

		tableViewer.getTable().addKeyListener(getKeyAdapter());
		
		return tableViewer;
	}

	protected boolean hasFocusBean(IHyperlink hyperlink) {
		if (fFocus == null) {
			return true;
		}
		if (hyperlink.equals(fFocus)) {
			return true;
		}

		return false;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInput(Object information) {
		if(!(information instanceof IHyperlink[])){
			inputChanged(null, null);
			return;
		}
		
		hyperlinks = (IHyperlink[])information;

		BeanTableContentProvider contentProvider= new BeanTableContentProvider(hyperlinks);
		getTableViewer().setContentProvider(contentProvider);


		inputChanged(hyperlinks, hyperlinks[0]);
	}

	protected void toggleHierarchy() {
		TableViewer tableViewer= getTableViewer();

		tableViewer.getTable().setRedraw(false);

		// reveal selection
		Object selectedElement= getSelectedElement();
		if (selectedElement != null)
			getTableViewer().reveal(selectedElement);
		else
			selectFirstMatch();

		tableViewer.getTable().setRedraw(true);

		updateStatusFieldText();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getSelectedElement() {
		Object selectedElement= super.getSelectedElement();
		return selectedElement;
	}

	@Override
	protected String getId() {
		return "org.jboss.tools.cdi.text.ext.InformationControl";
	}
	
	public static class BeanTableContentProvider implements IStructuredContentProvider{
		private IHyperlink[] hyperlinks;
		
		public BeanTableContentProvider(IHyperlink[] beans){
			this.hyperlinks = beans;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return hyperlinks;
		}

	}
	
	public static class BeanTableLabelProvider implements ILabelProvider{
		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		public void setFilter(ViewerFilter viewerFilter) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			if(element instanceof IInformationItem){
				return ((IInformationItem)element).getImage();
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if(element instanceof IHyperlink){
				if(element instanceof IInformationItem){
					String info = ((IInformationItem)element).getInformation();
					String qualifiedName = ((IInformationItem)element).getFullyQualifiedName();
					String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
					return info + " - " + packageName;
				}
				return ((IHyperlink)element).getHyperlinkText();
			}
			return "";
		}
		
	}
	
	public class BeanFilter extends ViewerFilter {
		SearchPattern patternMatcher = new SearchPattern();
		public boolean isConsistentItem(Object item) {
			return true;
		}

		public boolean select(Viewer viewer, Object parentElement,
	            Object element) {
			
			if (element instanceof IInformationItem) {
				String information = ((IInformationItem)element).getInformation();
				if(getFilterText().getText().isEmpty()){
					patternMatcher.setPattern("*");
				}else{
					patternMatcher.setPattern(getFilterText().getText());
				}
				return patternMatcher.matches(information);
			}else
				return true;
		}
	}
}

