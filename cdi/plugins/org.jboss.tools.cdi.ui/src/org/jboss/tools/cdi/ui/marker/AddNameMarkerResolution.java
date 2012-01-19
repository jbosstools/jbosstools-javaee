/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class AddNameMarkerResolution extends ChangeAnnotationMarkerResolution {
	private String parameter;

	public AddNameMarkerResolution(IAnnotation annotation, String parameter) {
		super(annotation, "\""+parameter+"\"");
		this.parameter = parameter;
		label = NLS.bind(CDIUIMessages.ADD_NAME_MARKER_RESOLUTION_TITLE, parameter);
		description = getPreview();
	}
	
	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_ADD;
	}
}
