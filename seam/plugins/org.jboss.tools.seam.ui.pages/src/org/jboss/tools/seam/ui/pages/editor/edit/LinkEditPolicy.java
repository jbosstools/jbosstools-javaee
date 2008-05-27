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

import org.eclipse.gef.commands.Command;

import org.eclipse.gef.requests.GroupRequest;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;



public class LinkEditPolicy
	extends org.eclipse.gef.editpolicies.ConnectionEditPolicy {
	
	protected Command getDeleteCommand(GroupRequest request) {
//		ConnectionCommand c = new ConnectionCommand();
//		c.setLink((Link)getHost().getModel());
//		return c;
		return null;
	}

}