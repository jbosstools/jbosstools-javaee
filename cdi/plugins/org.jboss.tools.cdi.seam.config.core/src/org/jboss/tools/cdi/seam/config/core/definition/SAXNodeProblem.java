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
package org.jboss.tools.cdi.seam.config.core.definition;

import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;

public class SAXNodeProblem {
	SAXNode node;
	String problemId;
	String message;

	public SAXNodeProblem(SAXNode node, String problemId, String message) {
		this.node = node;
		this.problemId = problemId;
		this.message = message;
	}

	public SAXNode getNode() {
		return node;
	}

	public String getProblemId() {
		return problemId;
	}

	public String getMessage() {
		return message;
	}

}
