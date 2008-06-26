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
package org.jboss.tools.jsf.ui.editor.actions;

import java.util.List;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.gef.commands.Command;
import org.jboss.tools.common.gef.action.DiagramCutAction;

final public class JSFCutAction extends DiagramCutAction {

	public JSFCutAction(IWorkbenchPart editor) {
		super(editor);
	}

	protected Command createCommand(List objects) {
		return JSFCommandFactory.createCutCommand(objects);
	}

}


