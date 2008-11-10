/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.w3c.dom.Element;

public abstract class AbstractEditableRichFacesTemplate extends
		VpeAbstractTemplate {

	public String getAttribute(Element sourceElement, String attributeName) {

		if (sourceElement.hasAttribute(attributeName))
			return sourceElement.getAttribute(attributeName);

		return Constants.EMPTY;
	}
}