/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.internal.contentassist;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.ui.JobImages;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class BatchJobCompletionProposalComputer extends AbstractXMLModelQueryCompletionProposalComputer implements BatchConstants {
	CompletionProposalInvocationContext context;
	IFile file;
	IBatchProject bp;

	@Override
	protected XMLContentModelGenerator getContentGenerator() {
		return new XMLContentModelGenerator();
	}

	@Override
	protected boolean validModelQueryNode(CMNode node) {
		return false;
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest, CompletionProposalInvocationContext context) {
		this.context = context;

		file = PageContextFactory.getResource(context.getDocument());
		bp = BatchCorePlugin.getBatchProject(file.getProject(), true);
		if(bp == null) {
			return;
		}

		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
		if(!(node instanceof Element)) {
			return;
		}
		Element current = (Element)node;

		int begin = contentAssistRequest.getReplacementBeginPosition();
		int length = contentAssistRequest.getReplacementLength();
		String matchString = contentAssistRequest.getMatchString();
		if(matchString.startsWith("\"")) {
			matchString = matchString.substring(1);
			begin++;
			length -= 2;
		}

		Node n = findNodeForOffset(node, context.getInvocationOffset());
		if(n instanceof Attr) {
			if(ATTR_REF.equals(n.getNodeName())) {
				addRefAttributeValueProposals(contentAssistRequest, context, current, matchString, begin, length);
			} else if(ATTR_NEXT.equals(n.getNodeName())) {
				addTransitionAttributeValueProposals(contentAssistRequest, context, current, matchString, begin, length, false);
			} else if(ATTR_TO.equals(n.getNodeName())) {
				current = (Element)current.getParentNode();
				if(current != null) {
					addTransitionAttributeValueProposals(contentAssistRequest, context, current, matchString, begin, length, false);
				}
			} else if(ATTR_RESTART.equals(n.getNodeName())) {
				while(current != null && current.getParentNode() != null && !current.getParentNode().getNodeName().equals(TAG_JOB)) {
					current = (Element)current.getParentNode();
				}
				if(current != null && current.getParentNode() != null && current.getParentNode().getNodeName().equals(TAG_JOB)) {
					addTransitionAttributeValueProposals(contentAssistRequest, context, current, matchString, begin, length, true);
				}
			} else if(ATTR_NAME.equals(n.getNodeName()) && TAG_PROPERTY.equals(current.getNodeName())) {
				addPropertyNameValueProposals(contentAssistRequest, context, current, matchString, begin, length);
			}
		}
	}
	
	private void addRefAttributeValueProposals(ContentAssistRequest contentAssistRequest, CompletionProposalInvocationContext context, 
			Element current, String matchString, int begin, int length) {
		String tag = current.getNodeName();
		Collection<IBatchArtifact> as = null;
		if(current.getParentNode() == null || !(current.getParentNode().getParentNode() instanceof Element)) {
			return;
		}
		if(TAG_LISTENER.equals(tag)) {
			Element p = (Element)current.getParentNode().getParentNode();
			if(p != null && p.getNodeName().equals(TAG_JOB)) {
				as = bp.getArtifacts(BatchArtifactType.JOB_LISTENER);
			} else {
				as = bp.getArtifacts(BatchArtifactType.STEP_LISTENER);
				Element chunk = XMLUtilities.getUniqueChild(p, TAG_CHUNK);
				if(chunk != null) {
					as = new HashSet<IBatchArtifact>();
					for (IBatchArtifact a: bp.getAllArtifacts()) {
						BatchArtifactType t = a.getArtifactType();
						if(TAG_STEP.equals(t.getTag())) {
							as.add(a);
						}
					}
				}
			}
		} else {
			for (BatchArtifactType t: BatchArtifactType.values()) {
				if(t.getTag().equals(tag)) {
					as = bp.getArtifacts(t);
				}
			}
		}
		if(as != null) {
			for (IBatchArtifact a: as) {
				String value = a.getName();
				if(!value.startsWith(matchString)) {
					continue;
				}
				CustomCompletionProposal proposal = new CustomCompletionProposal(
						value, begin, length, value.length(), 
						JobImages.getImageByElement(a.getArtifactType()), 
						value, null, value + ": " + a.getType().getElementName(), 
						TextProposal.R_XML_ATTRIBUTE_VALUE);
				contentAssistRequest.addProposal(proposal);
			}
		}				
	}

	private void addTransitionAttributeValueProposals(ContentAssistRequest contentAssistRequest, CompletionProposalInvocationContext context, 
			Element current, String matchString, int begin, int length, boolean currentAllowed) {
		String currentId = current.getAttribute(ATTR_ID);
		Element parent = (Element)current.getParentNode();
		Map<String, String> ids = new HashMap<String, String>();
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node cn = nl.item(i);
			if(cn instanceof Element) {
				String id1 = ((Element)cn).getAttribute(ATTR_ID);
				if(id1 != null && id1.length() > 0 && (currentAllowed || !id1.equals(currentId))) {
					ids.put(id1, cn.getNodeName());
				}
			}
		}
		for (String s: ids.keySet()) {
			if(!s.startsWith(matchString)) {
				continue;
			}
			CustomCompletionProposal proposal = new CustomCompletionProposal(
					s, begin, length, s.length(), 
					JobImages.getImage(JobImages.FLOW_IMAGE),
					s, null, "<" + ids.get(s) + " id=\"" + s + "\"" + ">", 
					TextProposal.R_XML_ATTRIBUTE_VALUE);
			contentAssistRequest.addProposal(proposal);
		}
	}

	private void addPropertyNameValueProposals(ContentAssistRequest contentAssistRequest, CompletionProposalInvocationContext context, 
			Element current, String matchString, int begin, int length) {
		Element properties = (Element)current.getParentNode();
		if(properties != null && TAG_PROPERTIES.equals(properties.getNodeName())) {
			Set<String> existing = new HashSet<String>();
			for (Element e: XMLUtilities.getChildren(properties, TAG_PROPERTY)) {
				if(e != current) existing.add(e.getAttribute(ATTR_NAME));
			}
			Set<String> variants = new HashSet<String>();
			Element artifactElement = (Element)properties.getParentNode();
			String ref = artifactElement.getAttribute(ATTR_REF).trim();
			if(ref != null && ref.length() > 0) {
				Collection<IBatchArtifact> a = bp.getArtifacts(ref);
				if(!a.isEmpty()) {
					for (IBatchProperty prop: a.iterator().next().getProperties()) {
						String nm = prop.getPropertyName();
						if(!existing.contains(nm)) {
							variants.add(nm);
						}
					}
				}				
			}
			while(artifactElement.getParentNode() instanceof Element) {
				artifactElement = (Element)artifactElement.getParentNode();
				properties = XMLUtilities.getUniqueChild(artifactElement, TAG_PROPERTIES);
				if(properties != null) {
					for (Element prop: XMLUtilities.getChildren(properties, TAG_PROPERTY)) {
						String nm = prop.getAttribute(ATTR_NAME);
						if(!existing.contains(nm)) {
							variants.add(nm);
						}
					}
				}
			}
			for (String s: variants) {
				if(!s.startsWith(matchString)) {
					continue;
				}
				CustomCompletionProposal proposal = new CustomCompletionProposal(
						s, begin, length, s.length(), 
						JobImages.getImage(JobImages.PROPERTY_IMAGE),
						s, null, s, 
						TextProposal.R_XML_ATTRIBUTE_VALUE);
				contentAssistRequest.addProposal(proposal);
			}
		}
	}

	/* Utility functions */
	Node findNodeForOffset(IDOMNode node, int offset) {
		if(node == null) return null;
		if (!node.contains(offset)) return null;
			
		if (node.hasChildNodes()) {
			// Try to find the node in children
			NodeList children = node.getChildNodes();
			for (int i = 0; children != null && i < children.getLength(); i++) {
				IDOMNode child = (IDOMNode)children.item(i);
				if (child.contains(offset)) {
					return findNodeForOffset(child, offset);
				}
			}
		}
			// Not found in children or nave no children
		if (node.hasAttributes()) {
			// Try to find in the node attributes
			NamedNodeMap attributes = node.getAttributes();
			
			for (int i = 0; attributes != null && i < attributes.getLength(); i++) {
				IDOMNode attr = (IDOMNode)attributes.item(i);
				if (attr.contains(offset)) {
					return attr;
				}
			}
		}
		// Return the node itself
		return node;
	}

}
