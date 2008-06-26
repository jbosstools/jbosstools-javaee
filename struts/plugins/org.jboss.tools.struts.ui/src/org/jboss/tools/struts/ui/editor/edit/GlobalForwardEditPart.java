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
package org.jboss.tools.struts.ui.editor.edit;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

import org.jboss.tools.struts.ui.editor.figures.GlobalForwardFigure;

public class GlobalForwardEditPart extends ProcessItemEditPart {
	protected Dimension calculatePreffSize() {
		prefferedSize.width = FigureUtilities.getTextExtents(getProcessItemModel().getName(), getProcessItemModel().getStrutsModel().getOptions().getActionFont()).width+38;
		return prefferedSize;
	}

	protected IFigure createFigure() {
		fig = new GlobalForwardFigure(getProcessItemModel(), this);
		return fig;
	}
}
