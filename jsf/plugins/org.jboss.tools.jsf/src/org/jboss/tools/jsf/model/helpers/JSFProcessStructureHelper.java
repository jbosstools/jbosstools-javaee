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
package org.jboss.tools.jsf.model.helpers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.markers.XMarkerManager;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.jst.web.model.helpers.WebProcessStructureHelper;

public class JSFProcessStructureHelper extends WebProcessStructureHelper implements JSFConstants {
	public static final JSFProcessStructureHelper instance = new JSFProcessStructureHelper(); 

	public XModelObject getParentProcess(XModelObject element) {
		XModelObject p = element;
		while(p != null && p.getFileType() == XModelObject.NONE &&
			  !"JSFProcess".equals(p.getModelEntity().getName())) p = p.getParent(); //$NON-NLS-1$
		return p;
	}

	public XModelObject[] getGroups(XModelObject process) {
		return process.getChildren(ENT_PROCESS_GROUP);
	}

	public XModelObject[] getItems(XModelObject group) {
		return group.getChildren(ENT_PROCESS_ITEM);
	}
	
	public boolean isMultiRuleGroup(XModelObject group) {
		return getItems(group).length > 1;
	}

	public XModelObject[] getOutputs(XModelObject item) {
		return item.getChildren(ENT_PROCESS_ITEM_OUTPUT);
	}

	public String getPath(XModelObject element) {
		return element.getAttributeValue(ATT_PATH);
	}

	public XModelObject getItemOutputTarget(XModelObject itemOutput) {
		return itemOutput.getParent().getParent().getParent().getChildByPath(itemOutput.getAttributeValue(ATT_TARGET));
	}
	
	public boolean isGroupPattern(XModelObject group) {
		String path = group.getAttributeValue(ATT_PATH);
		return (path != null) && (path.length() == 0 || path.indexOf('*') >= 0);
	}
	
	public String getPageTitle(XModelObject group) {
		return group.getPresentationString();
	}
	
	public String getItemOutputPresentation(XModelObject itemOutput) {
//		boolean s = isShortcut(itemOutput);
		return itemOutput.getPresentationString();
	}
	
	public static String createItemOutputPresentation(XModelObject reference) {
		if(reference == null) return null;
		String action = reference.getAttributeValue(ATT_FROM_ACTION);
		String prefix = (action == null || action.length() == 0) ? "" : action + ":"; //$NON-NLS-1$ //$NON-NLS-2$
		String outcome = reference.getAttributeValue(ATT_FROM_OUTCOME);
		if(outcome.length() == 0) outcome = JSFConstants.EMPTY_NAVIGATION_RULE_NAME;
		return prefix + outcome;		
		
	}
	
	public boolean isUnconfirmedPage(XModelObject group) {
		if(!(group instanceof ReferenceGroupImpl)) return false;
		if(isGroupPattern(group)) return false;
		return !"true".equals(group.getAttributeValue("confirmed")); //$NON-NLS-1$ //$NON-NLS-2$
	}

///	public static boolean isPageConfirmed(XModelObject itemPage) {
///		return "true".equals(itemPage.get("confirmed"));
///	}

	public XModelObject getReference(XModelObject diagramObject) {
		if(diagramObject instanceof ReferenceGroupImpl) {
			ReferenceGroupImpl g = (ReferenceGroupImpl)diagramObject;
			XModelObject[] rs = g.getReferences();
			if(rs.length > 0) return rs[0];
			String path = g.getAttributeValue(ATT_PATH);
			if(path == null) return null;
			XModelObject page = g.getModel().getByPath(path);
			return page;
		} else if(diagramObject instanceof ReferenceObject) {
			return ((ReferenceObject)diagramObject).getReference();
		}
		return null; 
	}
	
	public boolean hasErrors(XModelObject diagramObject){
		if(diagramObject instanceof ReferenceGroupImpl) {
			ReferenceGroupImpl g = (ReferenceGroupImpl)diagramObject;
			XModelObject[] rs = g.getReferences();
			if(rs.length > 0) {
				for (int i = 0; i < rs.length; i++) {
					if(XMarkerManager.getInstance().hasErrors(rs[i])) return true;
				}
			}
			if(isGroupPattern(diagramObject)) return false;
			String path = g.getAttributeValue(ATT_PATH);
			if(path != null) {
				XModelObject page = g.getModel().getByPath(path);
				return XMarkerManager.getInstance().hasErrors(page);
			}
		} else if(diagramObject instanceof ReferenceObject) {
			XModelObject reference = ((ReferenceObject)diagramObject).getReference();
			return XMarkerManager.getInstance().hasErrors(reference);
		}
		return XMarkerManager.getInstance().hasErrors(diagramObject);
	}

}
