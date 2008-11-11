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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.gef.commands.Command;
import org.jboss.tools.common.gef.action.DiagramDeleteAction;

final public class PagesDeleteAction extends DiagramDeleteAction {

	public PagesDeleteAction(IWorkbenchPart editor) {
		super(editor);
	}

	protected Command createCommand(List objects) {
		return PagesCommandFactory.createDeleteCommand(objects);
	}

}
