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
package org.jboss.tools.struts.ui.editor.dnd;

import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.common.model.ui.dnd.ModelTransfer;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.resources.ResourcesPlugin;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.ui.editor.StrutsEditor;
import org.jboss.tools.struts.ui.editor.edit.StrutsDiagramEditPart;


public class XModelTransferDropTargetListener 
	implements TransferDropTargetListener 
{
	
StrutsEditor editor;
boolean baseDropAccept = false;

public XModelTransferDropTargetListener(StrutsEditor editor) {
	this.editor = editor;
}

public void dragOperationChanged(DropTargetEvent event){
	
}

public void dragEnter(DropTargetEvent event){
	if(TemplateTransfer.getInstance().isSupportedType(event.currentDataType)){
		baseDropAccept = true;
		return;
	}else if(FileTransfer.getInstance().isSupportedType(event.currentDataType)){
		//baseDropAccept = false;
		String[] os = (String[])FileTransfer.getInstance().nativeToJava(event.currentDataType);
		if(os == null || os.length != 1 || !new File(os[0]).isFile())return; 
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(os[0]));
		if(f == null || !f.exists())return; 
		if(!DndHelper.drag(EclipseResourceUtil.getObjectByResource(f)))return; 
	}
	if(DndHelper.isDropEnabled(editor.getStrutsModel().getSource())){
			baseDropAccept = true;
		  } else {
			baseDropAccept = false;
		  }
}

public void dragOver(DropTargetEvent event){
	if(!baseDropAccept) event.detail = DND.DROP_NONE;
	else event.detail = DND.DROP_DEFAULT;

}

public void dragLeave(DropTargetEvent event){
}

public void dropAccept(DropTargetEvent event){
}

public void drop(DropTargetEvent event){
	org.eclipse.swt.graphics.Point parentPoint = editor.getScrollingGraphicalViewer().getControl().toControl(event.x, event.y);
	Point point = new Point(parentPoint.x, parentPoint.y);
	
	((StrutsDiagramEditPart)editor.getScrollingGraphicalViewer().getRootEditPart().getChildren().get(0)).getFigure().translateToRelative(point);
	if(TemplateTransfer.getInstance().isSupportedType(event.currentDataType)){
		Properties properties = new Properties();
		if(point != null){
		   properties.put("process.mouse.x","" + point.x);
		   properties.put("process.mouse.y","" + point.y);
		}

		XActionInvoker.invoke("CreateActions.AddRule", (XModelObject)editor.getStrutsModel().getSource(), properties);
		return;
	}
	DndHelper.drop(editor.getStrutsModel().getSource(), point);
}

public Transfer getTransfer(){
	return ModelTransfer.getInstance(); 
}

public boolean isEnabled(DropTargetEvent event) {
	 return true;
}

}
