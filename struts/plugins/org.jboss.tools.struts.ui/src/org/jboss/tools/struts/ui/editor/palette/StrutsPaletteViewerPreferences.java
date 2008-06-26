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
package org.jboss.tools.struts.ui.editor.palette;

import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;

import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.StrutsEditor;

/**
 * 
 * @author eskimo(dgolovin@exadel.com)
 *
 */
public class StrutsPaletteViewerPreferences extends DefaultPaletteViewerPreferences {

	public StrutsPaletteViewerPreferences(StrutsEditor editor) {
		super(StrutsUIPlugin.getDefault().getPreferenceStore());
	}
}
