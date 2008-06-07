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
package org.jboss.tools.seam.ui.pages.editor.commands;

import org.eclipse.gef.commands.Command;

import org.jboss.tools.seam.pages.xml.model.handlers.PageAdopt;
import org.jboss.tools.seam.ui.pages.editor.dnd.DndHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;

public class ReconnectSourceLinkCommand2 extends Command{
	
	static Page page = null;
	
	public ReconnectSourceLinkCommand2(){
		super("ReconnectSourceLinkCommand2");
	}
	
	public void setPage(Page page){
		ReconnectSourceLinkCommand2.page = page;
	}
	public boolean canExecute() {
		PageAdopt.move_case = true;
		try {
			return DndHelper.isDropEnabled(page.getData());
		} finally {
			PageAdopt.move_case = false;
		}
	}
	
	public void execute(){
		if(page != null) {
			PageAdopt.move_case = true;
			try {
				DndHelper.drop(page.getData());
			} finally {
				PageAdopt.move_case = false;
			}
		}
		page = null;
	}
	
	public boolean canUndo() {
		return false;
	}
}
