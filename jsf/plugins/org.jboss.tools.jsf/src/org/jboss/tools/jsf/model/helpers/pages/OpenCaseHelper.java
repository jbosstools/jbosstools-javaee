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
package org.jboss.tools.jsf.model.helpers.pages;

import java.util.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.pv.*;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.jst.web.project.WebProject;

public class OpenCaseHelper {
	
	public String run(XModel model, IFile jsp, String action) {
		if(model == null || jsp == null || action == null) return null;
		XModelObject jspObject = EclipseResourceUtil.getObjectByResource(jsp);
		if(jspObject == null) return null;
		WebProject p = WebProject.getInstance(model);
		String jspLocation = jsp.getLocation().toString().replace('\\', '/');
		String webRoot = p.getWebRootLocation().replace('\\', '/');
		if(webRoot.endsWith("/")) webRoot = webRoot.substring(0, webRoot.length() - 1); //$NON-NLS-1$
		if(!jspLocation.startsWith(webRoot)) return null;
		String viewPath = jspLocation.substring(webRoot.length());
		CaseSearchResult result = findCase(model, viewPath, action);
		if(result.ruleObject == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_MATCHING_RULE_FOR_PATH, viewPath);
		XModelObject object = result.getObject();
		
		XAction xaction = XActionInvoker.getAction("Select", object); //$NON-NLS-1$
		if(xaction != null && xaction.isEnabled(object)) {
			XActionInvoker.invoke("Select", object, new Properties()); //$NON-NLS-1$
		}
/*		
		if(result.caseObject == object) {
			xaction = XActionInvoker.getAction("OpenPage", object);
			if(xaction != null && xaction.isEnabled(object)) {
				XActionInvoker.invoke("OpenPage", object, new Properties());
			} else {
				return "Cannot open page \"" + object.getAttributeValue(JSFConstants.ATT_TO_VIEW_ID) + "\".";
			}
		} else {
			return "Cannot find matching case for action " + action + ".";
		}
*/
		return null;
	}
	
	class CaseSearchResult {
		String match = null;
		XModelObject caseObject = null;
		XModelObject ruleObject = null;
		public XModelObject getObject() {
			return caseObject != null ? caseObject : ruleObject;
		}
	}
	
	private CaseSearchResult findCase(XModel model, String viewPath, String action) {
		CaseSearchResult result = new CaseSearchResult();
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return result;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null) return result;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			XModelObject r = os[i].getChildByPath(JSFConstants.FOLDER_NAVIGATION_RULES);
			if(r == null) continue;
			XModelObject[] rs = r.getChildren(JSFConstants.ENT_NAVIGATION_RULE);
			for (int j = 0; j < rs.length; j++) {
				String fromViewId = rs[j].getAttributeValue(JSFConstants.ATT_FROM_VIEW_ID);
				if(!OpenCaseHelper.isPatternMatches(fromViewId, viewPath)) continue;
				XModelObject[] cs = rs[j].getChildren(JSFConstants.ENT_NAVIGATION_CASE);
				for (int k = 0; k < cs.length; k++) {
					String q1 = cs[k].getAttributeValue(JSFConstants.ATT_FROM_OUTCOME);
					String q2 = cs[k].getAttributeValue(JSFConstants.ATT_FROM_ACTION);
					if(!action.equals(q1) && !action.equals(q2)) continue;
					if(result.match == null || result.match.length() < fromViewId.length() || result.caseObject == null) {
						result.caseObject = cs[k];
						result.ruleObject = rs[j];
						result.match = fromViewId;
					}
				}
				if(result.match == null || (result.match.length() < fromViewId.length() && result.caseObject == null) || result.ruleObject == null) {
					result.ruleObject = rs[j];
					result.match = fromViewId;
				}
			}
		}
		return result;
	}

	public static boolean isPatternMatches(String pattern, String fromViewId) {
		if(pattern.length() == 0 || "*".equals(pattern)) return true;
		pattern = pattern.toLowerCase().replace('\\', '/');
		fromViewId = fromViewId.toLowerCase().replace('\\', '/');
		if(pattern.indexOf('*') < 0) return pattern.equals(fromViewId);
		StringTokenizer st = new StringTokenizer(pattern, "*", true);
		boolean f = true;
		while(st.hasMoreTokens()) {
			String t = st.nextToken();
			if("*".equals(t)) {
				f = false;
			} else {
				int i = fromViewId.indexOf(t);
				if(i < 0 || (f && i > 0)) return false;
				fromViewId = fromViewId.substring(i);
			}
		}
		return true;
	}
	
}
