/*******************************************************************************
 * Copyright (c) 2008 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.pages.editor.actions;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.seam.ui.pages.editor.commands.PagesCompoundCommand;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.edit.LinkEditPart;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesEditPart;

public class PagesCommandFactory {
	private static final String DELETE_ACTION = "DeleteActions.Delete";
	private static final String COPY_ACTION = "CopyActions.Copy";
	private static final String CUT_ACTION = "CopyActions.Cut";
	private static final String PASTE_ACTION = "CopyActions.Paste";
	
	private static Command createCommand(List objects, String commandPath) {
		Object source = null;
		if (objects.isEmpty())
			return null;
		if ((objects.get(0) instanceof PagesEditPart) || (objects.get(0) instanceof LinkEditPart)){
			PagesCompoundCommand compoundCmd = new PagesCompoundCommand(commandPath);
			for (int i = 0; i < objects.size(); i++) {
				source = null;
				if(objects.get(i) instanceof PagesEditPart) source = ((PagesElement)((PagesEditPart)objects.get(i)).getModel()).getData();
				else if(objects.get(i) instanceof LinkEditPart) source = ((Link)((LinkEditPart)objects.get(i)).getModel()).getData();
				if(source != null)compoundCmd.add(source);
			}
			return compoundCmd;
		} else return null;
	}

	public static Command createDeleteCommand(List objects) {
		return createCommand(objects, DELETE_ACTION);
	}

	public static Command createCopyCommand(List objects) {
		return createCommand(objects, COPY_ACTION);
	}
	
	public static Command createCutCommand(List objects) {
		return createCommand(objects, CUT_ACTION);
	}

	public static Command createPasteCommand(List objects) {
		return createCommand(objects, PASTE_ACTION);
	}

}
