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
package org.jboss.tools.struts.ui.editor.figures;

import org.eclipse.swt.graphics.Color;

import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class GlobalExceptionFigure extends GlobalForwardFigure {
	
	public GlobalExceptionFigure(IProcessItem processItem, ProcessItemEditPart part) {
		super(processItem, part);
		bgColor = new Color(null, 0xf9, 0xcf, 0xd0);
		fgColor = new Color(null, 0xca, 0x00, 0x00);
	}
}
