/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.pages.editor.edit;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.pde.internal.ui.util.PDEJavaHelperUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.AttributeContentProposalProviderFactory;
import org.jboss.tools.seam.pages.xml.SeamPagesXMLMessages;
import org.jboss.tools.seam.pages.xml.model.handlers.AddViewSupport;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.figures.ExceptionFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.PageFigure;

public class ViewIDEditManager extends DirectEditManager {

	private IActionBars actionBars;
	private CellEditorActionHandler actionHandler;
	private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
	private double cachedZoom = -1.0;
	private Font scaledFont;
	private ZoomListener zoomListener = new ZoomListener() {
		public void zoomChanged(double newZoom) {
			updateScaledFont(newZoom);
		}
	};

	private XModelObject target;
	
	private boolean rename = false;

	public ViewIDEditManager(GraphicalEditPart source, CellEditorLocator locator) {
		super(source, null, locator);
		target = (XModelObject) ((PagesDiagramEditPart) source.getParent())
				.getPagesModel().getData();
	}
	
	public ViewIDEditManager(GraphicalEditPart source, CellEditorLocator locator, boolean rename) {
		this(source, locator);
		this.rename = rename;
	}
	
	/**
	 * Creates and returns the DirectEditRequest.
	 * @return the direct edit request
	 */
	protected DirectEditRequest createDirectEditRequest() {
		DirectEditRequest req = super.createDirectEditRequest();
		if(rename){
			req.getExtendedData().put("rename", new Boolean(true));
		}
		return req;
	}

	/**
	 * @see org.eclipse.gef.tools.DirectEditManager#bringDown()
	 */
	protected void bringDown() {
		if(getEditPart().getParent() == null)return;
		ZoomManager zoomMgr = (ZoomManager) getEditPart().getViewer()
				.getProperty(ZoomManager.class.toString());
		if (zoomMgr != null)
			zoomMgr.removeZoomListener(zoomListener);

		if (actionHandler != null) {
			actionHandler.dispose();
			actionHandler = null;
		}
		if (actionBars != null) {
			restoreSavedActions(actionBars);
			actionBars.updateActionBars();
			actionBars = null;
		}

		super.bringDown();
		disposeScaledFont();

		PagesElement element = ((PagesEditPart) getEditPart())
				.getElementModel();
		if(!rename)
			element.setParent(null);
	}

	protected CellEditor createCellEditorOn(Composite composite) {
		return new TextCellEditor(composite, SWT.SINGLE | SWT.WRAP) {
			protected void setErrorMessage(String message) {
				super.setErrorMessage(message);
				((WorkbenchWindow) Workbench.getInstance()
						.getActiveWorkbenchWindow()).getStatusLineManager()
						.setErrorMessage(message);
			}

			protected void editOccured(ModifyEvent e) {
				String value = text.getText();
				if (value == null) {
					value = "";//$NON-NLS-1$
				}
				Object typedValue = value;
				boolean oldValidState = isValueValid();
				boolean newValidState = isCorrect(typedValue);
				if (typedValue == null && newValidState) {
					Assert
							.isTrue(false,
									"Validator isn't limiting the cell editor's type range");//$NON-NLS-1$
				}
				if (!newValidState) {
					// try to insert the current value into the error message.
					setErrorMessage(MessageFormat.format(getErrorMessage(),
							new Object[] { value }));
				} else {
					setErrorMessage("");
					valueChanged(oldValidState, newValidState);
				}
				
			}

			protected Control createControl(Composite parent) {
				Control control = super.createControl(parent);
				control.setBackground(ColorConstants.white);
				setErrorMessage("");
				return control;
			}
		};
	}

	private void disposeScaledFont() {
		if (scaledFont != null) {
			scaledFont.dispose();
			scaledFont = null;
		}
	}

	protected void initCellEditor() {
		NodeFigure figure = (NodeFigure) getEditPart().getFigure();
		if (figure instanceof PageFigure && ((PageFigure) figure).page.getName() != null)
			getCellEditor().setValue(((PageFigure) figure).page.getName());
		else if (figure instanceof ExceptionFigure && ((ExceptionFigure) figure).exc.getName() != null)
			getCellEditor().setValue(((ExceptionFigure) figure).exc.getName());
		else
			getCellEditor().setValue("");

		ZoomManager zoomMgr = (ZoomManager) getEditPart().getViewer()
				.getProperty(ZoomManager.class.toString());
		if (zoomMgr != null) {
			cachedZoom = -1.0;
			updateScaledFont(zoomMgr.getZoom());
			zoomMgr.addZoomListener(zoomListener);
		} else
			getCellEditor().getControl().setFont(figure.getFont());

		actionBars = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor().getEditorSite()
				.getActionBars();
		saveCurrentActions(actionBars);
		actionHandler = new CellEditorActionHandler(actionBars);
		actionHandler.addCellEditor(getCellEditor());
		actionBars.updateActionBars();
		getCellEditor().setValidator(new ViewIDValidator(target));
		if (figure instanceof PageFigure){
			XAttribute attr = target.getModel().getMetaData().getEntity("SeamPage21").getAttribute("view id");
			AttributeContentProposalProviderFactory.registerContentAssist(target, null, attr, getCellEditor().getControl());
		}else if(figure instanceof ExceptionFigure){
			IProject project = (IProject)target.getModel().getProperties().get("project");
			
			PDEJavaHelperUI.addTypeFieldAssistToText((Text)getCellEditor().getControl(), project, IJavaSearchConstants.CLASS);
		}
		
	}

	private void restoreSavedActions(IActionBars actionBars) {
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
				selectAll);
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
		actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), find);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
	}

	private void saveCurrentActions(IActionBars actionBars) {
		copy = actionBars.getGlobalActionHandler(ActionFactory.COPY.getId());
		paste = actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId());
		delete = actionBars
				.getGlobalActionHandler(ActionFactory.DELETE.getId());
		selectAll = actionBars.getGlobalActionHandler(ActionFactory.SELECT_ALL
				.getId());
		cut = actionBars.getGlobalActionHandler(ActionFactory.CUT.getId());
		find = actionBars.getGlobalActionHandler(ActionFactory.FIND.getId());
		undo = actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
		redo = actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
	}

	private void updateScaledFont(double zoom) {
		if (cachedZoom == zoom)
			return;

		Text text = (Text) getCellEditor().getControl();
		Font font = getEditPart().getFigure().getFont();

		disposeScaledFont();
		cachedZoom = zoom;
		if (zoom == 1.0)
			text.setFont(font);
		else {
			FontData fd = font.getFontData()[0];
			fd.setHeight((int) (fd.getHeight() * zoom));
			text.setFont(scaledFont = new Font(null, fd));
		}
	}
}

class ViewIDValidator implements ICellEditorValidator {
	private XModelObject target;
	
	public ViewIDValidator(XModelObject target){
		super();
		this.target = target;
	}

	private XModelObject getTarget() {
		return target;
	}

	public String isValid(Object value) {
		if (value == null)
			return null;

		String message = "";
		String viewID = value.toString();
		String path = AddViewSupport.revalidatePath(viewID, "");

		if (!AddViewSupport.isCorrectPath(path)) {
			message = SeamPagesXMLMessages.ATTRIBUTE_VIEW_ID_IS_NOT_CORRECT;
			return message;
		}

		String pp = SeamPagesDiagramHelper.toNavigationRulePathPart(path);
		boolean exists = getTarget().getChildByPath(pp) != null;
		if (exists) {
			message = "View exists.";
			return message;
		}
		return null;
	}
}