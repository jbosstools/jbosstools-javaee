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
package org.jboss.tools.jsf.ui.editor.dnd;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.dnd.ModelTransfer;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.JSFXModelUtil;
import org.jboss.tools.jsf.ui.editor.JSFEditor;
import org.jboss.tools.jsf.ui.editor.edit.JSFDiagramEditPart;

public class XModelTransferDropTargetListener implements TransferDropTargetListener {
	JSFEditor editor;

	boolean baseDropAccept = false;

	public XModelTransferDropTargetListener(JSFEditor editor) {
		this.editor = editor;
	}

	public void dragOperationChanged(DropTargetEvent event) {

	}

	public void dragEnter(DropTargetEvent event) {
		if (TemplateTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			baseDropAccept = true;
			return;
		} else if (FileTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			String[] os = (String[]) FileTransfer.getInstance().nativeToJava(
					event.currentDataType);
			if (os == null || os.length != 1 || !new File(os[0]).isFile())
				return;
			IFile f = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(new Path(os[0]));
			if (f == null || !f.exists())
				return;
			if (!DndHelper.drag(EclipseResourceUtil.getObjectByResource(f)))
				return;
		}
		if (DndHelper.isDropEnabled(editor.getJSFModel().getSource())) {
			baseDropAccept = true;
		} else {
			baseDropAccept = false;
		}
	}

	public void dragOver(DropTargetEvent event) {
		if (!baseDropAccept)
			event.detail = DND.DROP_NONE;
		else
			event.detail = DND.DROP_COPY;
	}

	public void dragLeave(DropTargetEvent event) {
	}

	public void dropAccept(DropTargetEvent event) {
	}

	public void drop(DropTargetEvent event) {
		org.eclipse.swt.graphics.Point parentPoint = editor
				.getScrollingGraphicalViewer().getControl().toControl(event.x,
						event.y);
		Point point = new Point(parentPoint.x, parentPoint.y);

		((JSFDiagramEditPart) editor.getScrollingGraphicalViewer()
				.getRootEditPart().getChildren().get(0)).getFigure()
				.translateToRelative(point);
		if (TemplateTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			Properties properties = new Properties();
			if (point != null) {
				properties.put("process.mouse.x", "" + point.x); //$NON-NLS-1$ //$NON-NLS-2$
				properties.put("process.mouse.y", "" + point.y); //$NON-NLS-1$ //$NON-NLS-2$
			}

			JSFXModelUtil.addRule((XModelObject) editor.getJSFModel().getSource(), properties);
			return;
		}
		DndHelper.drop(editor.getJSFModel().getSource(), point);
	}

	public Transfer getTransfer() {
		return ModelTransfer.getInstance();
	}

	public boolean isEnabled(DropTargetEvent event) {
		return true;
	}

}
