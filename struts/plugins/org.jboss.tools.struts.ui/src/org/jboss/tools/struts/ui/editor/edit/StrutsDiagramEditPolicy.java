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
package org.jboss.tools.struts.ui.editor.edit;

import java.util.Properties;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.editor.ActionTemplate;
import org.jboss.tools.struts.ui.editor.GlobalExceptionTemplate;
import org.jboss.tools.struts.ui.editor.GlobalForwardTemplate;
import org.jboss.tools.struts.ui.editor.PageTemplate;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;

public class StrutsDiagramEditPolicy extends RootComponentEditPolicy{

	public StrutsDiagramEditPolicy() {
		super();
	}
	
	public StrutsDiagramEditPart getDiagramEditPart(){
		return (StrutsDiagramEditPart)getHost();
	}
	
	public Command getCommand(Request request) {
		if (RequestConstants.REQ_CREATE.equals(request.getType())){
			CreateRequest req = (CreateRequest)request;
			if(req.getNewObjectType().equals(ActionTemplate.class))
				return getCreateActionCommand((CreateRequest)request);
			else if(req.getNewObjectType().equals(GlobalForwardTemplate.class))
				return getCreateForwardCommand((CreateRequest)request);
			else if(req.getNewObjectType().equals(GlobalExceptionTemplate.class))
				return getCreateExceptionCommand((CreateRequest)request);
			else if(req.getNewObjectType().equals(PageTemplate.class))
				return getCreatePageCommand((CreateRequest)request);
		}
		return super.getCommand(request);
	}
	
	public Command getCreateActionCommand(CreateRequest request){
		CreateActionCommand comm = new CreateActionCommand();
		comm.setLocation(request.getLocation());
		return comm;
	}

	public Command getCreateForwardCommand(CreateRequest request){
		CreateForwardCommand comm = new CreateForwardCommand();
		comm.setLocation(request.getLocation());
		return comm;
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
	
	class CreateActionCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			StrutsDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
			location = point;
		}
	
	
		public CreateActionCommand(){
			super("CreateActionCommand");
		}
	
		public boolean canExecute(){
			return true;
		}
	
		public void execute() {
			Properties properties = new Properties();
			if(location != null){
			   properties.put("process.mouse.x","" + location.x);
			   properties.put("process.mouse.y","" + location.y);
			}

			XActionInvoker.invoke("CreateActions.CreateAction", (XModelObject)((IStrutsModel)getDiagramEditPart().getModel()).getSource(),properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}
	
	class CreateForwardCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			StrutsDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
			location = point;
		}
	
	
		public CreateForwardCommand(){
			super("CreateForwardCommand");
		}
	
		public boolean canExecute(){
			return true;
		}
	
		public void execute(){
			Properties properties = new Properties();
			if(location != null){
			   properties.put("process.mouse.x","" + location.x);
			   properties.put("process.mouse.y","" + location.y);
			}

			XActionInvoker.invoke("CreateActions.CreateForward", (XModelObject)((IStrutsModel)getDiagramEditPart().getModel()).getSource(),properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}

	class CreateExceptionCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			StrutsDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
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
			   properties.put("process.mouse.x","" + location.x);
			   properties.put("process.mouse.y","" + location.y);
			}

			XActionInvoker.invoke("CreateActions.CreateException", (XModelObject)((IStrutsModel)getDiagramEditPart().getModel()).getSource(),properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}
	
	class CreatePageCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			StrutsDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
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
			   properties.put("process.mouse.x","" + location.x);
			   properties.put("process.mouse.y","" + location.y);
			}

			XActionInvoker.invoke("CreateActions.CreatePage", (XModelObject)((IStrutsModel)getDiagramEditPart().getModel()).getSource(),properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}
}
