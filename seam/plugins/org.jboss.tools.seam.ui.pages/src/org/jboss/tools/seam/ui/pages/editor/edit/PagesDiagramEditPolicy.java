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

import java.util.Properties;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesXModelUtil;
import org.jboss.tools.seam.ui.pages.editor.ExceptionTemplate;
import org.jboss.tools.seam.ui.pages.editor.PageTemplate;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;

public class PagesDiagramEditPolicy extends RootComponentEditPolicy{

	public PagesDiagramEditPolicy() {
		super();
	}
	
	public PagesDiagramEditPart getDiagramEditPart(){
		return (PagesDiagramEditPart)getHost();
	}
	
	public Command getCommand(Request request) {
		if (RequestConstants.REQ_CREATE.equals(request.getType())){
			CreateRequest req = (CreateRequest)request;
			if(req.getNewObjectType().equals(ExceptionTemplate.class))
				return getCreateExceptionCommand((CreateRequest)request);
			else if(req.getNewObjectType().equals(PageTemplate.class))
				return getCreatePageCommand((CreateRequest)request);
		}
		return super.getCommand(request);
	}
	
	public Command getCreateExceptionCommand(CreateRequest request){
		CreateExceptionCommand comm = new CreateExceptionCommand();
		comm.setLocation(request.getLocation());
		return comm;
	}
	
	public Command getCreatePageCommand(CreateRequest request){
		CreatePageCommand comm = new CreatePageCommand();
		comm.setLocation(request.getLocation());
		return comm;
	}
	
	class CreateExceptionCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			PagesDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
			location = point;
		}
	
	
		public CreateExceptionCommand(){
			super("CreateExceptionCommand");
		}
	
		public boolean canExecute(){
			return true;
		}
	
		public void execute(){
			Properties properties = new Properties();
			if(location != null){
			   properties.put("mouse.x","" + location.x);
			   properties.put("mouse.y","" + location.y);
			}
			properties.put("diagramEditPart", getDiagramEditPart());

			SeamPagesXModelUtil.addException((XModelObject)((PagesModel)getDiagramEditPart().getModel()).getData(), properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}
	
	class CreatePageCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			PagesDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
			location = point;
		}
	
	
		public CreatePageCommand(){
			super("CreatePageCommand");
		}
	
		public boolean canExecute(){
			return true;
		}
	
		public void execute(){
			Properties properties = new Properties();
			if(location != null) {
			   properties.put("mouse.x","" + location.x);
			   properties.put("mouse.y","" + location.y);
			}
			properties.put("diagramEditPart", getDiagramEditPart());

			SeamPagesXModelUtil.addPage((XModelObject)((PagesModel)getDiagramEditPart().getModel()).getData(), properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}
}
