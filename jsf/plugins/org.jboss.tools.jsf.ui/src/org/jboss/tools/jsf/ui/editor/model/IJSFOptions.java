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
package org.jboss.tools.jsf.ui.editor.model;

import org.eclipse.swt.graphics.Font;

public interface IJSFOptions {
	public boolean isGridVisible();
	public int getGridStep();
	public int getVisualGridStep();
	public Font getLinkPathFont();
	public Font getViewPathFont();
	public boolean switchToSelectionTool();
	public boolean showShortcutIcon();
	public boolean showShortcutPath();
}
