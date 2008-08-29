package org.jboss.tools.seam.ui.pages.editor;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.editor.ObjectTextEditor;
import org.jboss.tools.common.gef.outline.xpl.DiagramContentOutlinePage;
import org.jboss.tools.common.model.ui.editor.EditorDescriptor;
import org.jboss.tools.common.model.ui.texteditors.XMLTextEditorComponent;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesFilteredTreeConstraint;
import org.jboss.tools.seam.ui.pages.SeamUIPagesMessages;
import org.jboss.tools.seam.ui.pages.SeamUiPagesPlugin;

public class SeamPagesEditor extends ObjectMultiPageEditor {
	protected SeamPagesGuiEditor guiEditor;
	protected SeamPagesFilteredTreeConstraint constraint = new SeamPagesFilteredTreeConstraint();
	
	protected Composite createPageContainer(Composite parent) {
		Composite composite = super.createPageContainer(parent);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJSFHelpContextIds.FACES_CONFIG_EDITOR);
		return composite;
	}
	
	public SeamPagesEditor() {
		SeamPagesFilteredTreeConstraint constraint2 = new SeamPagesFilteredTreeConstraint();
		outline.addFilter(constraint2);
	}

	protected boolean isWrongEntity(String entity) {
		return !entity.startsWith(SeamPagesConstants.ENT_FILE_SEAM_PAGES);
	}

	protected void doCreatePages() {
		if (isAppropriateNature() || true /* JBIDE-541 */) {
			createGuiPage();
			treeFormPage = createTreeFormPage();
			treeFormPage.setTitle(SeamUIPagesMessages.SEAM_PAGES_EDITOR_TITLE); 
			treeFormPage.addFilter(constraint);
			treeFormPage.initialize(getModelObject());
			addFormPage(treeFormPage);
		}
		createTextPage();
		initEditors();
	}
	
	protected String[] getSupportedNatures() {
		return null;
	}

	protected String getNatureWarningMessageKey() {
		return "SharableEditors.natureWarning.jsf.message";
	}

	protected void createGuiPage() {
		try{
			guiEditor = new SeamPagesGuiEditor();
			guiEditor.init(getEditorSite(), getEditorInput());
			int index = addPage(guiEditor, input);
			setPageText(index, SeamUIPagesMessages.SEAM_PAGES_EDITOR_DIAGRAM_TAB); 
			guiEditor.setInput(input);
			selectionProvider.setHost(guiEditor.getSelectionProvider());		
			guiEditor.addErrorSelectionListener(createErrorSelectionListener());
			selectionProvider.addHost("guiEditor", guiEditor.getSelectionProvider());
		} catch(PartInitException ex) {
			SeamUiPagesPlugin.getDefault().logError(ex);
		}
	}
	
	protected ObjectTextEditor createTextEditor() {
		return new XMLTextEditorComponent();	
	}

	public void dispose() {
		super.dispose();
		if(guiEditor != null) {
			guiEditor.dispose();
			guiEditor = null;
		}
	}
	
	protected void setErrorMode() {
		setNormalMode();
	}
	
	protected void setNormalMode() {
		if(guiEditor != null) {
			guiEditor.setObject(getModelObject(), isErrorMode());
			updateSelectionProvider();
		}
		if (treeEditor != null) {		
			treeEditor.setObject(object, isErrorMode());
		}
		if (treeFormPage != null) {
			treeFormPage.initialize(getModelObject());
			treeFormPage.setErrorMode(isErrorMode());
		}
	}
	
	protected int getGuiPageIndex() {
		return 0; 
	}
	
	public boolean isGuiEditorActive() {
		return getActivePage() == getGuiPageIndex();
	}
	
	protected void updateSelectionProvider() {
		if(guiEditor != null) selectionProvider.addHost("guiEditor", guiEditor.getSelectionProvider());
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider());
		int index = getActivePage();
		if(index == getSourcePageIndex()) {
			if(textEditor != null) {
				selectionProvider.setHost(getTextSelectionProvider());
			}
			return;
		}
		if(index == 1 || guiEditor == null || guiEditor.getSelectionProvider() == null) {
			if (treeEditor != null) {
				selectionProvider.setHost(treeEditor.getSelectionProvider());
				treeEditor.fireEditorSelected();
			}
			if (treeFormPage != null) {
				selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider(), true);
			}
		} else {
			ISelectionProvider p = guiEditor.getSelectionProvider();
			selectionProvider.setHost(p);
			if(p instanceof AbstractSelectionProvider) {
				((AbstractSelectionProvider)p).fireSelectionChanged();
			}		
		}
	}
	
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if(newPageIndex != getGuiPageIndex()) {
		} else {
		}
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter == IContentOutlinePage.class){
			if(guiEditor == null || guiEditor.getGUI() == null) {
				return super.getAdapter(adapter);
			}
			Object o = guiEditor.getGUI().getAdapter(adapter);
			if(o instanceof DiagramContentOutlinePage) {
				DiagramContentOutlinePage g = (DiagramContentOutlinePage)o;
				g.setTreeOutline(outline);
			}
			return o;  
		}
		if(adapter == ActionRegistry.class || adapter == org.eclipse.gef.editparts.ZoomManager.class){
			 if(guiEditor != null)
			 	if(guiEditor.getGUI() != null)
			 		return guiEditor.getGUI().getAdapter(adapter);
		}
		if (adapter == EditorDescriptor.class)
			return new EditorDescriptor("faces-config");

		if(adapter == SeamPagesEditor.class) return this;
		return super.getAdapter(adapter);
	}
	
}
