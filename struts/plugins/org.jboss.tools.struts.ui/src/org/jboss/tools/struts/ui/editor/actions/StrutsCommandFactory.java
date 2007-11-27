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
package org.jboss.tools.struts.ui.editor.actions;

import java.util.List;
import org.eclipse.gef.commands.Command;
import org.jboss.tools.struts.ui.editor.edit.LinkEditPart;
import org.jboss.tools.struts.ui.editor.edit.StrutsEditPart;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;
import org.jboss.tools.struts.ui.editor.model.commands.StrutsCompoundCommand;

public class StrutsCommandFactory {
	
	private static Command createCommand(List objects, String commandPath) {
		Object source = null;
		if (objects.isEmpty())
			return null;
		if ((objects.get(0) instanceof StrutsEditPart) || (objects.get(0) instanceof LinkEditPart)){
			StrutsCompoundCommand compoundCmd = new StrutsCompoundCommand(commandPath);
			for (int i = 0; i < objects.size(); i++) {
				source = null;
				if(objects.get(i) instanceof StrutsEditPart) source = ((IStrutsElement)((StrutsEditPart)objects.get(i)).getModel()).getSource();
				else if(objects.get(i) instanceof LinkEditPart) source = ((IStrutsElement)((LinkEditPart)objects.get(i)).getModel()).getSource();
				if(source != null)compoundCmd.add(source);
			}
			return compoundCmd;
		} else return null;
	}

	public static Command createDeleteCommand(List objects) {
		return createCommand(objects, "DeleteActions.Delete");
	}

	public static Command createCopyCommand(List objects) {
		return createCommand(objects, "CopyActions.Copy");
	}
	
	public static Command createCutCommand(List objects) {
		return createCommand(objects, "CopyActions.Cut");
	}

	public static Command createPasteCommand(List objects) {
		return createCommand(objects, "CopyActions.Paste");
	}

}
