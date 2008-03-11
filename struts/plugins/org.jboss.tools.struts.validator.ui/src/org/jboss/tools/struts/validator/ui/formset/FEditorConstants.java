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
package org.jboss.tools.struts.validator.ui.formset;

import org.eclipse.swt.graphics.*;
import org.jboss.tools.common.model.util.*;

public interface FEditorConstants {
	public Color DEFAULT_COLOR = new Color(null, 0, 0, 0);
	public Color INHERITED = new Color(null, 125, 125, 0);	
	public Image IMAGE_EMPTY = EclipseResourceUtil.getImage("images/actions/empty.gif");
	public Image IMAGE_OK = EclipseResourceUtil.getImage("images/actions/ok.gif");
	public Image IMAGE_CANCEL = EclipseResourceUtil.getImage("images/actions/cancel.gif");
	public Image IMAGE_DELETE = EclipseResourceUtil.getImage("images/actions/delete.gif");
	public Image IMAGE_EDIT = EclipseResourceUtil.getImage("images/actions/edit.gif");
	public Image IMAGE_CREATE = EclipseResourceUtil.getImage("images/actions/new.gif");
	public Image IMAGE_CREATE_FORMSET = EclipseResourceUtil.getImage("images/struts/pro/new_validator_formset.gif");
}
