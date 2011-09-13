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
package org.jboss.tools.cdi.seam.config.core.definition;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class TextSourceReference implements ITextSourceReference {
	XModelObject fileObject;
	IResource resource;
	SAXNode node;

	public TextSourceReference(XModelObject fileObject, IResource resource, SAXNode node) {
		this.fileObject = fileObject;
		this.resource = resource;
		this.node = node;
	}

	public int getStartPosition() {
		return node.getLocation().getStartPosition();
	}

	public int getLength() {
		return node.getLocation().getLength();
	}

	public IResource getResource() {
		return resource;
	}

	public String toString() {
		//Used in AssignableBeansDialog.
		//If alternative usage is needed, should be moved to another interface.
		StringBuffer sb = new StringBuffer();
		sb.append(FileAnyImpl.toFileName(fileObject));
		sb.append(", line=").append(node.getLocation().getLine());
		sb.append(" - ");
		if(resource != null) {
			sb.append(resource.getFullPath().toString());
		} else if(fileObject != null) {
			String path = fileObject.getPath();
			XModelObject s = fileObject;
			while(s != null && s.getFileType() != XModelObject.SYSTEM) s = s.getParent();
			sb.append("/").append(EclipseResourceUtil.getProject(fileObject).getName());
			sb.append("/").append(s.getAttributeValue("name")).append(path.substring(s.getPath().length()));
		}
		return sb.toString();
	}

}
