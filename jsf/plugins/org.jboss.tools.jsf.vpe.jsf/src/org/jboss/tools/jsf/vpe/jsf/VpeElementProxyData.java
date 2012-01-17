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
package org.jboss.tools.jsf.vpe.jsf;

import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.w3c.dom.NodeList;

/**
 *
 */
public class VpeElementProxyData extends VpeElementData {

	private NodeList nodelist;

	public NodeList getNodelist() {
		return nodelist;
	}

	public void setNodelist(NodeList nodelist) {
		this.nodelist = nodelist;
	}

}
