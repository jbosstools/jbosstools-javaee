/*******************************************************************************
 * Copyright (c) 2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractAttribute;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractComponent;

public class TLDAttributeHyperlink extends TLDTagHyperlink {
	protected AbstractAttribute attr;
	
	public TLDAttributeHyperlink(AbstractAttribute attr, IRegion region) {
		super((AbstractComponent)attr.getComponent(), region);
		this.attr = attr;
		
		XModelObject attrObject = TLDTagHyperlink.getXModelObject(attr);
		if(attrObject != null) {
			xmodelObject = attrObject;
			if(xmodelObject != null && file != null) {
				String fileName = file.getName();
				String libraryName = getFileName(xmodelObject);
				String objectName = xmodelObject.getAttributeValue(XModelObjectConstants.ATTR_NAME);
				if(objectName == null) {
					objectName = xmodelObject.getAttributeValue("attribute-name");
				}
				xmodelObjectName = fileName;
				if(libraryName != null && !libraryName.equals(fileName)) {
					xmodelObjectName += " : " + libraryName;
				}
				if(objectName != null && !objectName.equals(libraryName)) {
					xmodelObjectName += " : " + objectName;
				}
			}
		}
	}
	
}
