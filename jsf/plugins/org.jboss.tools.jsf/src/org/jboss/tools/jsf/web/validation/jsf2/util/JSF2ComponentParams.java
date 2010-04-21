 /*******************************************************************************
  * Copyright (c) 2007-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.util;

import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class JSF2ComponentParams {

	private String relativeLocation;

	private JSF2ComponentParams() {

	}

	public static JSF2ComponentParams create(IDOMElement element) {
		JSF2ComponentParams componentParams = new JSF2ComponentParams();
		if (element != null) {
			ElementImpl elementImpl = (ElementImpl) element;
			String nameSpaceURI = elementImpl.getNamespaceURI();
			if (nameSpaceURI == null
					|| nameSpaceURI.indexOf(JSF2ResourceUtil.JSF2_URI_PREFIX) == -1) {
				return null;
			}
			String nodeName = element.getNodeName();
			if (nodeName.lastIndexOf(':') != -1) {
				nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
			}
			String relativeLocation = nameSpaceURI.replaceFirst(
					JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
			componentParams.setRelativateLocation(relativeLocation
					+ "/" + nodeName + ".xhtml"); //$NON-NLS-1$ //$NON-NLS-2$

		}
		return componentParams;
	}

	public void setRelativateLocation(String relativateLocation) {
		this.relativeLocation = relativateLocation;
	}

	public String getRelativateLocation() {
		return relativeLocation;
	}

}
