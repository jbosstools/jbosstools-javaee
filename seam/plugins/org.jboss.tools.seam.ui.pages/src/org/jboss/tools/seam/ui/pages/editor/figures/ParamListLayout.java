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
package org.jboss.tools.seam.ui.pages.editor.figures;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

public class ParamListLayout extends AbstractLayout {

	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		return new Dimension(SWT.DEFAULT, SWT.DEFAULT);
	}

	/**
	 * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
	 */
	public void layout(IFigure container) {
		Rectangle bounds = container.getBounds();
		
		int y = 0;

		for (int i = 0; i < container.getChildren().size(); i++) {
			IFigure figure = (IFigure) container.getChildren().get(i);
			figure.setLocation(new Point(bounds.x + 1, bounds.y + y + 1));
			figure.setSize(container.getSize().width - 2, 19);
			y += 19;
		}
	}
}
