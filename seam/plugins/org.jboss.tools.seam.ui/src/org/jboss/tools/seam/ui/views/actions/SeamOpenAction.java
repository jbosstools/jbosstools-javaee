/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.SeamCoreMessages;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamOpenAction extends Action {
	IOpenableElement element;
	
	public SeamOpenAction(IOpenableElement element) {
		setText(SeamCoreMessages.SEAM_OPEN_ACTION_OPEN);
		this.element = element;
	}
	
	@Override
	public void run() {
		if(element != null) {
			element.open();
		}		
	}	

}
