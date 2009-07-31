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
package org.jboss.tools.seam.xml.components.model.helpers;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.seam.xml.SeamXMLMessages;

//see OpenCaseHelper
public class OpenRuleHelper {
	
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
		XModelObject result = findRule(model, viewPath, action);
		if(result == null) 
			return NLS.bind(SeamXMLMessages.CANNOT_FIND_MATCHING_RULE_FOR_PATH, viewPath);
		XModelObject object = result;
		
		XAction xaction = XActionInvoker.getAction("Select", object); //$NON-NLS-1$
		if(xaction != null && xaction.isEnabled(object)) {
			XActionInvoker.invoke("Select", object, new Properties()); //$NON-NLS-1$
		}

		return null;
	}
	
	private XModelObject findRule(XModel model, String viewPath, String action) {
		XModelObject webinf = FileSystemsHelper.getWebInf(model);
		if(webinf == null) return null;
		XModelObject pagesXML = webinf.getChildByPath("pages.xml"); //$NON-NLS-1$
		if(pagesXML == null) return null;
		XModelObject pagesFolder = pagesXML.getChildByPath("Pages");
		XModelObject[] ps = pagesFolder.getChildren();
		
		for (int i = 0; i < ps.length; i++) {
			String viewId = ps[i].getAttributeValue("view id");
			if(!isPatternMatches(viewId, viewPath)) continue;
			XModelObject[] ns = ps[i].getChildren();
			for (int j = 0; j < ns.length; j++) {
				String entity = ns[j].getModelEntity().getName();
				if(!entity.startsWith("SeamPageNavigation")) continue;
				if(entity.startsWith("SeamPageNavigationRule")) continue;
				XModelObject[] rs = ns[j].getChildren();
				for (int k = 0; k < rs.length; k++) {
					String a = rs[k].getAttributeValue("if outcome");
					if(a != null && a.equals(action)) {
						//TODO compare match length
						return rs[k];
					}
				}
			}
		}
		
		return null;
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
