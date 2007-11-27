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

import org.eclipse.draw2d.IFigure;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.figures.GlobalExceptionFigure;

public class GlobalExceptionEditPart extends GlobalForwardEditPart {
	public void doDoubleClick(boolean cf) {
		try {
			XAction action = DnDUtil.getEnabledAction((XModelObject)getProcessItemModel().getSource(), null, "OpenSource");
			if(action != null) action.executeHandler((XModelObject)getProcessItemModel().getSource(),null);
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}
	protected IFigure createFigure() {
		fig = new GlobalExceptionFigure(getProcessItemModel(),this);
		return fig;
	}
}
