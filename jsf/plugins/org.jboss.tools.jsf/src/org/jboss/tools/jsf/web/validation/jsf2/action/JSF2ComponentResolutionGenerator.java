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

package org.jboss.tools.jsf.web.validation.jsf2.action;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.composite.CompositeComponentValidator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2ComponentResolutionGenerator implements
		IMarkerResolutionGenerator2 {
	
	public static final String JSF2_URI_PREFIX = "http://java.sun.com/jsf/composite"; //$NON-NLS-1$

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			int messageId = getMessageID(marker);
			if (messageId == -1)
				return new IMarkerResolution[] {};
	
			final IFile file = (IFile) marker.getResource();
	
			Integer attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
			if (attribute == null)
				return new IMarkerResolution[] {};
			final int start = attribute.intValue();
			
			IModelManager manager = StructuredModelManager.getModelManager();
			if(manager != null){
				IStructuredModel model = null;
				try {
					model = manager.getModelForRead(file);
					if (model instanceof IDOMModel) {
						Node node = Utils.findNodeForOffset(((IDOMModel) model).getDocument(), start);
						
						String tagName = node.getLocalName();
						String attrName = "";
						if(node instanceof IDOMAttr){
							attrName = node.getLocalName();
							node = ((IDOMAttr)node).getOwnerElement();
							tagName = node.getLocalName();
						}
					
						if (messageId == CompositeComponentValidator.UNKNOWN_COMPOSITE_COMPONENT_NAME_ID) {
							return new IMarkerResolution[] { new JSF2CompositeComponentProposal(marker.getResource(), getComponentPath(node), tagName, getAttributes(node)) };
						}else if(messageId == CompositeComponentValidator.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE_ID){
							
							return new IMarkerResolution[] { new JSF2CompositeAttrsProposal(marker.getResource(), getComponentPath(node), tagName, getAttributes(node), attrName) };
						}
					}
				} catch (CoreException e) {
					JSFModelPlugin.getPluginLog().logError(e);
				} catch (IOException e) {
					JSFModelPlugin.getPluginLog().logError(e);
				} finally {
					if (model != null) {
						model.releaseFromRead();
					}
				}
			}			
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return new IMarkerResolution[0];
	}
	
	private String getComponentPath(Node node){
		String path;
		String uriString = node.getNamespaceURI();
		String relativeLocation = uriString.replaceFirst(
				JSF2_URI_PREFIX, ""); //$NON-NLS-1$
		String nodeName = node.getLocalName();
		path = relativeLocation + "/" + nodeName + ".xhtml"; //$NON-NLS-1$ //$NON-NLS-2$
		return path;
	}
	
	private String[] getAttributes(Node node){
		NamedNodeMap nm = node.getAttributes();
		String[] attributes = new String[nm.getLength()];
		for(int i = 0; i < nm.getLength(); i++){
			attributes[i] = nm.item(i).getLocalName();
		}
		return attributes;
	}
	
	/**
	 * return message id or -1 if impossible to find
	 * @param marker
	 * @return
	 */
	private int getMessageID(IMarker marker)throws CoreException{
		Integer attribute = ((Integer) marker.getAttribute(CompositeComponentValidator.MESSAGE_ID_ATTRIBUTE_NAME));
		if (attribute != null)
			return attribute.intValue();

		return -1; 
	}


	public boolean hasResolutions(IMarker marker) {
		try {
			return getMessageID(marker) >= 0;
		} catch (CoreException ex) {
			JSFModelPlugin.getDefault().logError(ex);
		}
		return false;
	}


}
