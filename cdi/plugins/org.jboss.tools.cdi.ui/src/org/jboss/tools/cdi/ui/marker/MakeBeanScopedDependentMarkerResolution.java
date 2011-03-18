/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.ui.CDIUIMessages;

/**
 * @author Daniel Azarov
 */
public class MakeBeanScopedDependentMarkerResolution implements IMarkerResolution2{
	private String label;
	private IBean bean;
	private IFile file;
	
	public MakeBeanScopedDependentMarkerResolution(IBean bean, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_BEAN_SCOPED_DEPENDENT_MARKER_RESOLUTION_TITLE, new Object[]{bean.getBeanClass().getElementName()});
		this.bean = bean;
		this.file = file;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

}
