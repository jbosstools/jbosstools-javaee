/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// This template defines the toggling methods but doesn't implements VpeToggableTemplate 
// because of external toggle control. 

public class RichFacesTogglePanelTemplate extends VpeAbstractTemplate {

	private static Map toggleMap = new HashMap();
	private Map states = null;

	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		nsIDOMElement div = visualDocument.createElement("div"); //$NON-NLS-1$

		VpeCreationData creationData = new VpeCreationData(div);

		states = getStates(sourceElement);
		
		String state = getActiveState(sourceElement);
		
		Node bodyFacet = null;
		if(state!=null) {
			bodyFacet = ComponentUtil.getFacet(sourceElement, state, true);
		} else {
			ArrayList<Node> facets = ComponentUtil.getFacets(sourceElement, true);
			if(!facets.isEmpty()) {
				bodyFacet = facets.get(0);
			}
		}

		if(bodyFacet!=null) {
			VpeChildrenInfo bodyInfo = new VpeChildrenInfo(div);
			bodyInfo.addSourceChild(bodyFacet);
			creationData.addChildrenInfo(bodyInfo);
		}

		return creationData;
	}

	private Map getStates (Element sourceElement) {
		NodeList children = sourceElement.getChildNodes();
		HashMap states = new HashMap();
		
		for (int i = 0; children != null && i < children.getLength(); i++) {
			Node child = children.item(i);
			
			if (child instanceof Element && child.getNodeName().endsWith(":facet")) { //$NON-NLS-1$
				Element facet = (Element)child;
				String name = ((Element)facet).getAttribute("name"); //$NON-NLS-1$
				if (name != null) {
					states.put(name, facet);
				}
			}
		}
		return states;
	}
	
	private String getInitialState(Element sourceElement) {
		String initialState = sourceElement.getAttribute("initialState"); //$NON-NLS-1$
		
		String stateOrder = sourceElement.getAttribute("stateOrder"); //$NON-NLS-1$
		if(stateOrder!=null) {
			StringTokenizer st = new StringTokenizer(stateOrder.trim(), ",", false); //$NON-NLS-1$
			String firstState = null;
			while(st.hasMoreElements()) {
				String state = st.nextToken().trim();
				if (null == firstState) firstState = state;
				if (null != initialState) {
					if (initialState.equals(state) && states != null && states.containsKey(state))
						return initialState;
				} else {
					if (states != null && states.containsKey(state))
						return state;
				}
			}
		} else {
			if (null != initialState) {
				if (states != null && states.containsKey(initialState))
					return initialState;
			}
		}

		return (states == null || states.keySet().isEmpty() ? null : (String)states.keySet().iterator().next());
	}

	
	
	private String getActiveState(Element sourceElement) {
		String activeStateStr;
		String stateOrder = sourceElement.getAttribute("stateOrder"); //$NON-NLS-1$
		if(null == stateOrder)  return null;

		activeStateStr = (String)toggleMap.get(sourceElement);

		if (activeStateStr == null) {
			activeStateStr = getInitialState(sourceElement);
		}
		
		return activeStateStr;
	}

	private String getNextState(Element sourceElement, String toggleId) {
		String stateOrder = sourceElement.getAttribute("stateOrder"); //$NON-NLS-1$
		if(null == stateOrder)  return null;
		String activeState = getActiveState(sourceElement);
		
		StringTokenizer st = new StringTokenizer(stateOrder.trim(), ",", false); //$NON-NLS-1$
		String firstState = null;
		while (st.hasMoreElements()) {
			String state = st.nextToken();
			if (null == firstState) {
				firstState = state;
			}
			if (null != toggleId && toggleId.trim().length() != 0) {
				if(toggleId.equals(state)) {
					return state;
				}
			} else if (null != activeState) {
				if (activeState.equals(state)) {
					break;
				}
			}
		}
		// Stop searching with the next state (or first if the state was last in the chain)
		String newActiveState = null;
		if (st.hasMoreElements()) {
			newActiveState = st.nextToken();
		}
		if (null == newActiveState || newActiveState.trim().length() == 0) {
			newActiveState = firstState;
		}
		return newActiveState;
	}
	
	public void toggle(Node sourceNode, String toggleId) {
		Element sourceElement = (Element)(sourceNode instanceof Element ? sourceNode : sourceNode.getParentNode());
		if (null == toggleId || toggleId.trim().length() == 0) {
			toggleId = getNextState(sourceElement, null);
		} else {
			toggleId = getNextState(sourceElement, toggleId);
		}
		if (toggleId == null) return;
		toggleMap.put(sourceNode, toggleId);
	}

	public void stopToggling(Node sourceNode) {
		toggleMap.remove(sourceNode);
	}
}