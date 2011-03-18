/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.pages.editor;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.jboss.tools.common.model.ui.texteditors.AbstractMultiPageContributor;
import org.jboss.tools.common.text.xml.xpl.GoToMatchingTagAction;
import org.jboss.tools.common.text.xml.xpl.ToggleOccurencesMarkUpAction;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.jboss.tools.common.gef.action.ActionRegistrySupport;

/**
 * @author Jeremy
 */
public class PagesMultiPageContributor extends AbstractMultiPageContributor {
	protected FakeTextEditor fakeTextEditor = new FakeTextEditor();
	ActionRegistrySupport registrySupport = new ActionRegistrySupport();

	public PagesMultiPageContributor() {
		fToggleOccurencesMarkUp = new ToggleOccurencesMarkUpAction();
		
		ResourceBundle resourceBundle = XMLUIMessages.getResourceBundle();
		fGoToMatchingTagAction = new GoToMatchingTagAction(resourceBundle, "gotoMatchingTag_", null); //$NON-NLS-1$
		fGoToMatchingTagAction.setActionDefinitionId(GO_TO_MATCHING_TAG_ID);
		fGoToMatchingTagAction.setId(GO_TO_MATCHING_TAG_ID);
	}
	
	public void dispose() {
		registrySupport.dispose();
		super.dispose();
	}
	
	public void init(IActionBars bars) {
		if(registrySupport != null) registrySupport.dispose();
		registrySupport = new ActionRegistrySupport();
		registrySupport.setPage(getPage());
		super.init(bars);
		registrySupport.buildGEFActions();
		//registrySupport.addRetargetAction(new CutRetargetAction());
		registrySupport.declareGlobalActionKeys();
		registrySupport.contributeGEFToToolBar(bars.getToolBarManager());
		initEditMenu(bars);
	}

	public void setActivePage(IEditorPart part) {
		cleanStatusLine();
		fActiveEditorPart = part;	
		IActionBars actionBars = getActionBars();		
		if (actionBars != null) {

			ActionRegistry registry = null;
			if(fActiveEditorPart instanceof SeamPagesGuiEditor)
				registry = (ActionRegistry)part.getAdapter(ActionRegistry.class);
			Iterator<String> globalActionKeys = registrySupport.getGlobalActionKeys();
			while(globalActionKeys.hasNext()) {
				String id = (String)globalActionKeys.next();				
				actionBars.setGlobalActionHandler(id, (registry == null ? null : registry.getAction(id)));
			}
			IToolBarManager tbm = actionBars.getToolBarManager();
			IContributionItem [] items = tbm.getItems();
			if(items != null){
				for(int i = 0; i < items.length; i++){
					String id = items[i].getId();
					if(id == null) continue; //Separator
					actionBars.setGlobalActionHandler(id, (registry==null ? null:registry.getAction(id)));
				}
			}
	
			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;
			
			if (editor!=null) {
	
				actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), (registry != null) ? registry.getAction(ActionFactory.DELETE.getId()) : (editor != null) ? getAction(editor, ActionFactory.DELETE.getId()) : null);
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor, ActionFactory.UNDO.getId()));
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor, ActionFactory.REDO.getId()));
				actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), (registry != null) ? registry.getAction(ActionFactory.CUT.getId()) : (editor != null) ? getAction(editor, ActionFactory.CUT.getId()) : null);
				actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), (registry != null) ? registry.getAction(ActionFactory.COPY.getId()) : (editor != null) ? getAction(editor, ActionFactory.COPY.getId()) : null);
				actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), (registry != null) ? registry.getAction(ActionFactory.PASTE.getId()) : (editor != null) ? getAction(editor, ActionFactory.PASTE.getId()) : null);
				actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor, ActionFactory.SELECT_ALL.getId()));
				actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor, ActionFactory.FIND.getId()));
				actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(editor, IDEActionFactory.BOOKMARK.getId()));
				actionBars.setGlobalActionHandler(IDEActionFactory.ADD_TASK.getId(), getAction(editor, IDEActionFactory.ADD_TASK.getId()));
				actionBars.setGlobalActionHandler(ActionFactory.PRINT.getId(), (registry != null) ? registry.getAction("Print_Diagram") : (editor != null) ? getAction(editor, ActionFactory.PRINT.getId()) : null);
				actionBars.setGlobalActionHandler(ActionFactory.REVERT.getId(), getAction(editor, ActionFactory.REVERT.getId()));
				actionBars.setGlobalActionHandler(ActionFactory.SAVE.getId(), getAction(editor, ActionFactory.SAVE.getId()));
				actionBars.setGlobalActionHandler(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS, getAction(editor, StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS));				
			} else {
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
			}
			// re-register action on key binding service
			IEditorPart localPart = (part!=null)?part:mainPart;
			IHandlerService handler = (IHandlerService)localPart.getEditorSite().getService(IHandlerService.class);
			if(registry != null) {
				registerKeyBindings(handler, ACTIONS_1, registry);
			} else if (editor != null) {
				// editor
				registerKeyBindings(handler, ACTIONS_2, editor);
			} else {
				//fakeTextEditor
				registerKeyBindings(handler, ACTIONS_1, fakeTextEditor);
			}

			cleanActionBarStatus();
			actionBars.updateActionBars();
		}

		ITextEditor textEditor = getTextEditor(part);

		if(fToggleOccurencesMarkUp != null) {
			fToggleOccurencesMarkUp.setEditor(textEditor);
			fToggleOccurencesMarkUp.update();
		}
	
		fGoToMatchingTagAction.setEditor(textEditor);
		if (textEditor != null) {
			textEditor.setAction(GO_TO_MATCHING_TAG_ID, fGoToMatchingTagAction);
		}

		updateStatus();
	}

	public void registerKeyBindings(IHandlerService handler, String[] actions, ActionRegistry registry) {
		for (int i = 0; i < actions.length; i++) {
			IAction action = registry.getAction(actions[i]);
			registerKeyBinding(handler, actions[i], action);
		}
	}

	class FakeTextEditor extends AFakeTextEditor {
		public boolean canDoOperation(int operation) {
			return true;
		}
		public void doOperation(int operation) {
			if(fActiveEditorPart instanceof SeamPagesGuiEditor){
				ActionRegistry registry= (ActionRegistry)fActiveEditorPart.getAdapter(ActionRegistry.class);
				switch(operation){
					case 3: // cut
					registry.getAction(ActionFactory.CUT.getId()).run();
					break;
					case 4: // copy
					registry.getAction(ActionFactory.COPY.getId()).run();
					break;
					case 5: // paste
					registry.getAction(ActionFactory.PASTE.getId()).run();
					break;
				}
			}
		}
	}
}
