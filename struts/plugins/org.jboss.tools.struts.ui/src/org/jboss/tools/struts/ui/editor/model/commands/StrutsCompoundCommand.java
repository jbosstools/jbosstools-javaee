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
package org.jboss.tools.struts.ui.editor.model.commands;

import java.util.Vector;

import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.eclipse.gef.commands.Command;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.StrutsUIPlugin;

public class StrutsCompoundCommand extends Command{
	private Vector elements = new Vector();
	private String actionName;

	public StrutsCompoundCommand(String actionName) {
		super();
		this.actionName = actionName;
	}
	
	public boolean canExecute() {
		if(elements.size() > 0){
			XModelObject[] objects = (XModelObject[])elements.toArray(new XModelObject[]{});
			XModelObject object= objects[0];
			if(elements.size() == 1) objects = null;
			try{
				XAction action = DnDUtil.getEnabledAction(object, objects, actionName);
				if(action != null) return true;
				else return false;
			}catch(Exception ex){
				StrutsUIPlugin.getPluginLog().logError(ex);
				return false;
			}

		}else return false;
	}
	
	public boolean canUndo() {
		return false;
	}
	
	public void add(Object element){
		elements.add(element);
	}
	
	public void execute() {
		XModelObject[] objects = (XModelObject[])elements.toArray(new XModelObject[]{});
		XModelObject object= objects[0];
		if(elements.size() == 1) objects = null;
	    XActionInvoker.invoke(actionName, object, objects, null);
	}
}
