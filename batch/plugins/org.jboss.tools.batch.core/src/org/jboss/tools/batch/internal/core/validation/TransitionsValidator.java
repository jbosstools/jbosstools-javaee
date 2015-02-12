/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.internal.core.preferences.BatchSeverityPreferences;
import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class TransitionsValidator implements BatchConstants {
	JobTransitionsValidator jobTransitions;
	BatchValidator validator;
	Map<String, FlowNode> flowElements = new HashMap<String, FlowNode>();

	public TransitionsValidator(BatchValidator validator, JobTransitionsValidator jobLevel) {
		this.validator = validator;
		this.jobTransitions = jobLevel;
	}

	public String addFlowElement(Element element) {
		String id = element.getAttribute(ATTR_ID).trim();
		if(id.length() > 0) {
			FlowNode n = new FlowNode(element);
			if(!flowElements.containsKey(id)) {
				flowElements.put(id, n);
			}
		}
		return id;
	}

	public void validate(IFile file) {
		for (FlowNode n: flowElements.values()) {
			String fromNodeId = n.getIDValue();
			validateTransitionAttribute(n.element, fromNodeId, ATTR_NEXT, file);
			for (Element nextElement: XMLUtilities.getChildren(n.element, TAG_NEXT)) {
				validateTransitionAttribute(nextElement, fromNodeId, ATTR_TO, file);
			}
			for (Element nextElement: XMLUtilities.getChildren(n.element, TAG_STOP)) {
				validateRestartAttribute(nextElement, fromNodeId, file);
			}
		}

		while(reduce() > 0) {}
		for (FlowNode n: flowElements.values()) {
			String fromNodeId = n.getIDValue();
			for (FlowLink link: n.out) {
				String toNodeId = link.toNodeId;
				SimpleReference ref = new SimpleReference(link.attr.getOwnerElement(), link.attr.getName(), file);
				validator.addProblem(BatchValidationMessages.LOOP_IS_DETECTED, BatchSeverityPreferences.LOOP_IS_DETECTED,
						new String[]{fromNodeId, toNodeId}, ref.getLength(), ref.getStartPosition(), file/*, quickFixId*/);
			}
		}
	}

	void validateTransitionAttribute(Element fromElement, String fromNodeId, String attrName, IFile file) {
		Attr next = fromElement.getAttributeNode(attrName);
		if(next != null) {
			String toNodeId = fromElement.getAttribute(attrName).trim();
			if(toNodeId.length() > 0) {
				if(flowElements.get(toNodeId) == null) {
					validator.addProblem(BatchValidationMessages.TARGET_NOT_FOUND, BatchSeverityPreferences.TARGET_NOT_FOUND, fromElement, attrName, file, -1);
				} else if(toNodeId.equals(fromNodeId)) {
					validator.addProblem(BatchValidationMessages.TRANSITION_TO_SELF, BatchSeverityPreferences.LOOP_IS_DETECTED, fromElement, attrName, file, -1);
				} else {
					FlowLink link = new FlowLink(fromNodeId, next, toNodeId);
					flowElements.get(fromNodeId).out.add(link);
					flowElements.get(toNodeId).in.add(link);
				}
			}
		}
	}

	void validateRestartAttribute(Element fromElement, String fromNodeId, IFile file) {
		Attr next = fromElement.getAttributeNode(ATTR_RESTART);
		if(next != null) {
			String toNodeId = fromElement.getAttribute(ATTR_RESTART).trim();
			if(toNodeId.length() > 0) {
				if(!jobTransitions.ids.contains(toNodeId)) {
					validator.addProblem(BatchValidationMessages.TARGET_NOT_FOUND_ON_JOB_LEVEL, BatchSeverityPreferences.TARGET_NOT_FOUND, fromElement, ATTR_RESTART, file, -1);
				}
			}
		}
	}

	private int reduce() {
		String[] ids = flowElements.keySet().toArray(new String[0]);
		int removedLinks = 0;
		for (String id: ids) {
			FlowNode n = flowElements.get(id);
			if(n.in.isEmpty()) {
				for (FlowLink link: n.out) {
					FlowNode to = flowElements.get(link.toNodeId);
					if (to != null) {
						if(to.in.remove(link)) removedLinks++;
					}
				}
				flowElements.remove(id);
			} else if(n.out.isEmpty()) {
				for (FlowLink link: n.in) {
					FlowNode from = flowElements.get(link.fromNodeId);
					if (from != null) {
						if(from.out.remove(link)) removedLinks++;
					}
				}
				flowElements.remove(id);
			}
		}
		return removedLinks;
	}
	
	static class FlowNode {
		Element element;
		Attr id;

		Set<FlowLink> in = new HashSet<FlowLink>();
		Set<FlowLink> out = new HashSet<FlowLink>();

		public FlowNode(Element element) {
			this.element = element;
			id = element.getAttributeNode(ATTR_ID);
		}

		public String getIDValue() {
			return element.getAttribute(ATTR_ID).trim();
		}
	}

	static class FlowLink {
		String fromNodeId;
		String toNodeId;
		Attr attr;
	
		public FlowLink(String fromNodeId, Attr attr, String toNodeId) {
			this.fromNodeId = fromNodeId;
			this.toNodeId = toNodeId;
			this.attr = attr;
		}
	}

}

class JobTransitionsValidator extends TransitionsValidator {
	Set<String> ids = new HashSet<String>();
	
	public JobTransitionsValidator(BatchValidator validator) {
		super(validator, null);
		this.jobTransitions = this;
	}

	public String addFlowElement(Element element) {
		String id = super.addFlowElement(element);
		if(id != null && id.length() > 0) {
			ids.add(id);
		}
		return id;
	}
}