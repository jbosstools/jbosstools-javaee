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

import java.util.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;
import org.jboss.tools.common.gef.action.DiagramPasteAction;

final public class StrutsPasteAction extends DiagramPasteAction {

	public StrutsPasteAction(IWorkbenchPart editor) {
		super(editor);
	}

	protected Command createCommand(List objects) {
		return StrutsCommandFactory.createPasteCommand(objects);
	}

}
