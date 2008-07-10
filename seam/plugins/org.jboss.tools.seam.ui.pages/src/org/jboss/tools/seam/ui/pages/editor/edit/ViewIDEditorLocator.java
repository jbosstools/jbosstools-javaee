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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;

final public class ViewIDEditorLocator implements CellEditorLocator {

	private NodeFigure nodeFigure;

	public ViewIDEditorLocator(NodeFigure nodeFigure) {
		setFigure(nodeFigure);
	}

	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Rectangle rect = nodeFigure.getClientArea();
		nodeFigure.translateToAbsolute(rect);
		org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
		rect.translate(trim.x, trim.y);
		rect.width += trim.width;
		rect.height = 14;
		text.setBounds(rect.x+25, rect.y+2, rect.width-27, rect.height);
	}

	/**
	 * Returns the node figure.
	 */
	protected NodeFigure getFigure() {
		return nodeFigure;
	}

	/**
	 * Sets the node figure.
	 * 
	 */
	protected void setFigure(NodeFigure nodeFigure) {
		this.nodeFigure = nodeFigure;
	}

}