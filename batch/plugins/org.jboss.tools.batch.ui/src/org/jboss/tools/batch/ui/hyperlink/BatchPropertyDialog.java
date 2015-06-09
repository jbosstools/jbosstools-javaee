/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.hyperlink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.PatternFilter;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchUtil;
import org.jboss.tools.batch.internal.core.impl.BatchUtil.NodePathTextSourceReference;
import org.jboss.tools.batch.ui.JobImages;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.text.ext.hyperlink.xpl.AbstractBaseHyperlink;
import org.jboss.tools.common.text.ext.util.StructuredSelectionHelper;
import org.jboss.tools.jst.web.kb.KbQuery.Tag;

public class BatchPropertyDialog extends PopupDialog {
	private IBatchProperty batchProperty;
	private HashMap<IFile, List<NodePathTextSourceReference>> references = new HashMap<IFile, List<NodePathTextSourceReference>>();

	private Composite composite;

	private Text fFilterText;
	private TreeViewer tree;
	private ReferencePatternFilter patternFilter;

	public BatchPropertyDialog(Shell parentShell, final IBatchProperty batchProperty) {
		super(parentShell, 0, true, true, false, false, false, "", null);
		this.batchProperty = batchProperty;
		BusyIndicator.showWhile(parentShell != null ? parentShell.getDisplay() : Display.getDefault(),
		new Runnable() {
			@Override
			public void run() {
				for (IFile file : batchProperty.getArtifact().getProject().getDeclaredBatchJobs()) {
					List<NodePathTextSourceReference> list = BatchUtil.getNodePathPropertyAttributeReferences(file,
							batchProperty.getArtifact().getName(),
							batchProperty.getPropertyName());
					if(list.size() > 0){
						references.put(file, list);
					}
				}
			}
		});
	}
	
	/**
	 * for test purpose
	 * @return
	 */
	public HashMap<IFile, List<NodePathTextSourceReference>> getDisplayedReferences(){
		return references;
	}

	private String computeTitle() {
		StringBuffer result = new StringBuffer();
		
		result.append(NLS.bind(BatchHyperlinkMessages.SHOWING_REFERENCES_FOR_BATCH_PROPERTY, batchProperty.getPropertyName()));

		return result.toString();
	}
	
	@Override
	protected Control getFocusControl() {
		return getFilterText();
	}
	
	@Override
	protected Control createTitleMenuArea(Composite parent) {
		Composite fViewMenuButtonComposite= (Composite) super.createTitleMenuArea(parent);
		fFilterText = createFilterText(parent);
		return fViewMenuButtonComposite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(BatchHyperlinkMessages.BATCH_PROPERTY_REFERENCES);
	}

	@Override
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

		setTitleText(computeTitle());
		createTreeView(composite);
		composite.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
				close();
			}
		});
		installFilter();
		return composite;
	}

	
	void createTreeView(Composite parent) {
		tree = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		tree.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		GridData g = new GridData(GridData.FILL_BOTH);
		
		tree.getControl().setLayoutData(g);
		tree.setContentProvider(new TreeContent());
		tree.setLabelProvider(new LabelProvider());
		tree.setInput(batchProperty);
		patternFilter = new ReferencePatternFilter();
		tree.setFilters(new ViewerFilter[]{patternFilter});
		tree.addOpenListener(new IOpenListener() {			
			@Override
			public void open(OpenEvent event) {
				ISelection s = event.getSelection();
				if(!s.isEmpty() && s instanceof IStructuredSelection) {
					Object o = ((IStructuredSelection)s).getFirstElement();
					if(o instanceof NodePathTextSourceReference) {
						openElement((NodePathTextSourceReference) o);
						close();
					}
				}
			}
		});

		tree.getTree().addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				ViewerCell cell = tree.getCell(new Point(e.x, e.y));
				if(cell != null) {
					Widget w = cell.getItem();
					if(w != null && w.getData() != null) {
						tree.setSelection(new StructuredSelection(w.getData()));
					}
				}
			}
		});
		tree.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				gotoSelectedElement();
			}
		});
		

		tree.refresh();
	}

	protected void gotoSelectedElement() {
		ISelection s = tree.getSelection();
		if(!s.isEmpty() && s instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection)s).getFirstElement();
			if(o instanceof NodePathTextSourceReference) {
				openElement((NodePathTextSourceReference) o);
				close();
			}
		}
	}

	
	protected Text getFilterText() {
		return fFilterText;
	}

	protected Text createFilterText(Composite parent) {
		fFilterText= new Text(parent, SWT.NONE);
		Dialog.applyDialogFont(fFilterText);

		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment= GridData.FILL;
		data.verticalAlignment= GridData.CENTER;
		fFilterText.setLayoutData(data);
		

		fFilterText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) // return
					gotoSelectedElement();
				if (e.keyCode == SWT.ARROW_DOWN)
					tree.getTree().setFocus();
				if (e.keyCode == SWT.ARROW_UP)
					tree.getTree().setFocus();
				if (e.character == 0x1B) // ESC
					close();
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// do nothing
			}
		});
		return fFilterText;
	}

	private void installFilter() {
		fFilterText.setText(""); //$NON-NLS-1$

		fFilterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				//textFilter.filter();
				patternFilter.setPattern(fFilterText.getText());
				tree.refresh();
				tree.expandAll();
			}
		});
	}

	@Override
	protected Point getDefaultSize() {
		return new Point(700, 300);
	}

	@Override
	protected Point getDefaultLocation(Point size) {
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		if(display.getActiveShell() == null) {
			return super.getDefaultLocation(size);
		}
		Rectangle b = display.getActiveShell().getBounds();
		int x = b.x + (b.width - size.x) / 2;
		int y = b.y + (b.height - size.y) / 2;
		return new Point(x, y);
	}
	
	class ReferencePatternFilter extends PatternFilter{
		protected boolean isLeafMatch(Viewer viewer, Object element){
			if(element instanceof NodePathTextSourceReference){
				boolean parentMatch = super.isLeafMatch(viewer, ((NodePathTextSourceReference) element).getResource());
				if(parentMatch){
					return true;
				}
			}
			return super.isLeafMatch(viewer, element);
	    }
	}
	
	class TreeContent implements ITreeContentProvider {

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
			if(parentElement instanceof IBatchProperty){
				return references.keySet().toArray();
			}else if(parentElement instanceof IFile){
				return references.get((IFile)parentElement).toArray();
			}
			return null;
		}
	
		@Override
		public Object getParent(Object element) {
			if(element instanceof NodePathTextSourceReference){
				return ((ITextSourceReference) element).getResource();
			}
			return null;
		}
	
		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof IBatchProperty){
				return references.keySet().size() > 0;
			}else if(element instanceof IFile){
				return references.get((IFile)element).size() > 0;
			}
			return false;
		}

	}

	static Color gray = new Color(null, 128, 128, 128);
	static Color black = Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
	
	static Styler FILE_NAME_STYLER = new DefaultStyler(black, true, false);
	static Styler PATH_STYLER = new DefaultStyler(gray, true, false);
	static Styler PROPERTY_PATH_STYLER = new DefaultStyler(black, false, false);

	private static class DefaultStyler extends Styler {
		private Color foreground;
		private boolean bold;
		private boolean italic;

		public DefaultStyler(Color foreground, boolean bold, boolean italic) {
			this.foreground = foreground;
			this.italic = italic;
			this.bold = bold;
		}

		@Override
		public void applyStyles(TextStyle textStyle) {
			if (foreground != null) {
				textStyle.foreground = foreground;
			}
			if(italic) {
				textStyle.font = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
			}
			if(bold) {
				textStyle.font = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
			}
		}
	}

	class LabelProvider extends StyledCellLabelProvider implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, ILabelProvider {
		@Override
		public void update(ViewerCell cell) {
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

		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof IFile){
				StyledString sb = new StyledString();

				sb.append(((IFile)element).getName(), FILE_NAME_STYLER).append(" - ");
				
				sb.append(((IFile)element).getParent().getFullPath().toString(), PATH_STYLER);
				
				return sb;
			}else if(element instanceof NodePathTextSourceReference){
				return getNodePath((NodePathTextSourceReference)element);
			}
			return null;
		}

		@Override
		public Image getImage(Object element) {
			if(element instanceof IFile){
				return JobImages.getImage(JobImages.JOB_IMAGE);
			}else if(element instanceof NodePathTextSourceReference){
				return JobImages.getImage(JobImages.PROPERTY_IMAGE);
			}
			return null;
		}
	}
	
	private static StyledString getNodePath(NodePathTextSourceReference element){
		StyledString ss = new StyledString();
		List<Tag> tags = (List<Tag>) element.getNodePath();
		for(int index = tags.size()-1; index >= 0; index--){
			Tag tag = tags.get(index);
			if(BatchConstants.TAG_JOB.equalsIgnoreCase(tag.getName())){
				continue;
			}
			Map<String, String> attributes = tag.getAttributes();
			String attributeValue = null;
			String attributeName = BatchConstants.ATTR_ID;
			attributeValue = attributes.get(attributeName);
			if(attributeValue == null){
				attributeName = BatchConstants.ATTR_REF;
				attributeValue = attributes.get(attributeName);
			}
			ss.append("/", PROPERTY_PATH_STYLER);
			ss.append(tag.getName(), PROPERTY_PATH_STYLER);
			if(attributeValue != null){
				ss.append("(", PATH_STYLER ).append(attributeName, PATH_STYLER).append("=\"", PATH_STYLER).append(attributeValue, PATH_STYLER).append("\")", PATH_STYLER);
			}
			
		}
		return ss;
	}
	
	private void openElement(NodePathTextSourceReference reference){
		AbstractBaseHyperlink.openFileInEditor((IFile)reference.getResource());
		
		StructuredSelectionHelper.setSelectionAndRevealInActiveEditor(new Region(reference.getStartPosition(), reference.getLength()));
	}
}