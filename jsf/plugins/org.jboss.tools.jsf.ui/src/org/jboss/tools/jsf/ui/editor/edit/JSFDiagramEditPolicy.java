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
package org.jboss.tools.jsf.ui.editor.edit;

import java.util.Properties;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.model.JSFXModelUtil;
import org.jboss.tools.jsf.ui.editor.model.impl.JSFModel;

public class JSFDiagramEditPolicy extends RootComponentEditPolicy{

	public JSFDiagramEditPolicy() {
		super();
	}
	
	public JSFDiagramEditPart getDiagramEditPart(){
		return (JSFDiagramEditPart)getHost();
	}
	
	public Command getCommand(Request request) {
		if (RequestConstants.REQ_CREATE.equals(request.getType()))
			return getCreateCommand((CreateRequest)request);
		
		return super.getCommand(request);
	}
	
	public Command getCreateCommand(CreateRequest request){
		if(!request.getNewObjectType().equals(String.class)) return null;
		CreateViewCommand comm = new CreateViewCommand();
		comm.setLocation(request.getLocation());
		return comm;
	}
	
	class CreateViewCommand extends org.eclipse.gef.commands.Command{
		Point location;
	
		public void setLocation(Point point){
			JSFDiagramEditPolicy.this.getDiagramEditPart().getFigure().translateToRelative(point);
			location = point;
		}
	
	
		public CreateViewCommand(){
			super("CreateViewCommand"); //$NON-NLS-1$
		}
	
		public boolean canExecute(){
			return true;
		}
	
		public void execute(){
			Properties properties = new Properties();
			if(location != null){
			   properties.put("process.mouse.x","" + location.x); //$NON-NLS-1$ //$NON-NLS-2$
			   properties.put("process.mouse.y","" + location.y); //$NON-NLS-1$ //$NON-NLS-2$
			}

			JSFXModelUtil.addRule((XModelObject)((JSFModel)getDiagramEditPart().getModel()).getSource(), properties);
		}
	
		public boolean canUndo() {
			return false;
		}
	}

}
