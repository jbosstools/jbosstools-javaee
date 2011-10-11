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
