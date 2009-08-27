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
package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;

public class DiagramAdopt implements XAdoptManager, SeamPagesConstants {

	public void adopt(XModelObject target, XModelObject object, java.util.Properties p) throws XModelException {
		if(isAdoptableJSP(target, object)) {
			adoptJSP(target, object, p);
		} else if(isAdoptableItem(target, object)) {
			adoptItem(target, object, p);
		}
	}

	public boolean isAdoptable(XModelObject target, XModelObject object) {
		if(isAdoptableJSP(target, object)) return true;
		if(isAdoptableItem(target, object)) return true;
		return false;
	}
	
	static String ADOPTABLE_JSP = "." + ENT_FILEJSP + "." 
	                                  + ENT_FILEHTML + "." 
	                                  + ENT_FILEXHTML + "."
	                                  + "FileXML" + ".";

	private boolean isAdoptableJSP(XModelObject target, XModelObject object) {
		String entity = object.getModelEntity().getName();
		if (ADOPTABLE_JSP.indexOf("." + entity + ".") >= 0) {
			String path = XModelObjectLoaderUtil.getResourcePath(object);
			if (target.getModelEntity().getName().startsWith(ENT_FILE_SEAM_PAGES)) {
				target = target.getChildByPath(ELM_DIAGRAM);
			}
			return SeamPagesDiagramHelper.getHelper(target).getPage(path) == null;
		}
		return false;
	}

	private void adoptJSP(XModelObject target, XModelObject object, Properties p) throws XModelException {
		if (target.getModelEntity().getName().startsWith(ENT_FILE_SEAM_PAGES)) {
			target = target.getChildByPath(ELM_DIAGRAM);
		}
		addRuleByPageAdopt(target, object, p);
/*		
		Properties runningProperties = new Properties();
		runningProperties.put("preselectedObject", object);
		if(p != null) runningProperties.putAll(p);
		XActionInvoker.invoke("CreateActions.CreatePage", target, runningProperties);
*/
	}

	private void addRuleByPageAdopt(XModelObject diagram, XModelObject page, Properties p) throws XModelException {
		String path = XModelObjectLoaderUtil.getResourcePath(page);
		XModelObject pageItem = SeamPagesDiagramHelper.getHelper(diagram).getPage(path);
		if(pageItem != null) return;
		boolean doNotCreateEmptyRule = false; //or read preferences, compare JSF
		pageItem = SeamPagesDiagramHelper.getHelper(diagram).findOrCreateItem(path, null, TYPE_PAGE);
		setShape(pageItem, p);
		if(doNotCreateEmptyRule) {
			pageItem.setAttributeValue("persistent", "true");
			pageItem.setModified(true);
		} else {
			XModelObject pages = diagram.getParent().getChildByPath(FOLDER_PAGES);
			String suffix = getPageSuffix(diagram.getParent().getModelEntity().getName());
			XModelObject rule = pages.getModel().createModelObject(ENT_SEAM_PAGE + suffix, null);
			rule.setAttributeValue(ATTR_VIEW_ID, path);
			DefaultCreateHandler.addCreatedObject(pages, rule, p);
		}
	}

	public static String getPageSuffix(String entity) {
		if(entity.endsWith(SUFF_12)) {
			return SUFF_12;
		} else if(entity.endsWith(SUFF_20)) {
			return SUFF_20;
		} else if(entity.endsWith(SUFF_21)) {
			return SUFF_21;
		} else {
			return SUFF_22;
		}
	}
	
	public static void setShape(XModelObject group, Properties p) {
		String x = (p == null) ? null : p.getProperty("mouse.x");
		String y = (p == null) ? null : p.getProperty("mouse.y");
		if(x != null && y != null) {
			group.setAttributeValue("shape", "" + x + "," + y + ",0,0");
		}
	}
	
	private boolean isAdoptableItem(XModelObject target, XModelObject object) {
		return ENT_DIAGRAM_ITEM.equals(object.getModelEntity().getName());
	}
	
	private void adoptItem(XModelObject target, XModelObject object, Properties p) {
//		String path = object.getAttributeValue(ATT_PATH);
		p.put("sample", object);
		String type = object.getAttributeValue(ATTR_TYPE);
		if(TYPE_PAGE.equals(type)) {
			XActionInvoker.invoke("CreateActions.AddPageWizard", target, p);
		} else {
			//TODO
		}
	}

}
