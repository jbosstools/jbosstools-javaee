/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.bean.model.impl;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class FacesConfigDefinition {
	IPath path;
	XModelObject facesConfig;

	boolean isMetadataComplete;

	public void setPath(IPath path) {
		this.path = path;
	}

	public void setObject(XModelObject facesConfig) {
		this.facesConfig = facesConfig;
		isMetadataComplete = "true".equals(facesConfig.getAttributeValue("metadata-complete"));
	}

	public IPath getPath() {
		return path;
	}

	public boolean isMetadataComplete() {
		return isMetadataComplete;
	}
}
