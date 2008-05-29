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
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class PageAdopt implements XAdoptManager, SeamPagesConstants {
	
	public static boolean move_case = false;

	public boolean isAdoptable(XModelObject target, XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(ENT_PROCESS_ITEM_OUTPUT.equals(entity)) {
			if(move_case) {
				return canMoveCase(target, object);
			}
			return canBeOutputTarget(target);
		} 
		if(ENT_PROCESS_ITEM.equals(entity)) {
			return canBeOutputTarget(target);
		}
		if(entity.startsWith(ENT_SEAM_PAGE)) {
			return canBeOutputTarget(target);
		}
		return false;
	}
	
	private boolean canBeOutputTarget(XModelObject group) {
		String path = group.getAttributeValue("path");
		if(path == null) path = group.getAttributeValue(ATTR_VIEW_ID);
		if(path == null) return false;
		if(path.length() == 0 || path.indexOf("*") >= 0) return false;
		return true;
	}
	
	private boolean canMoveCase(XModelObject target, XModelObject object) {
		XModelObject case_ = null;
		if(object instanceof ReferenceObject) {
			case_ = ((ReferenceObject)object).getReference();
		} else {
			case_ = object;
		}
		XModelObject rule = null;
		if(target instanceof ReferenceObject) {
			rule = ((ReferenceObject)target).getReference();
		} else {
			rule = target;
		}
		return (case_ != null && rule == null || rule != case_.getParent());
	}

	public void adopt(XModelObject target, XModelObject object, Properties p) throws XModelException {
		String entity = object.getModelEntity().getName();
		if(ENT_PROCESS_ITEM_OUTPUT.equals(entity)) {
			if(move_case) {
				moveOutput(object, target, p);
			} else {
				adoptOutput(object, target, p);
			}
		}
		else if(ENT_PROCESS_ITEM.equals(entity)) adoptItem(object, target, p);
		else if(entity.startsWith(ENT_SEAM_PAGE)) adoptSeamPage(object, target, p);
	}
	
	protected void adoptOutput(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceObject i = (ReferenceObject)source;
		adoptCase(i.getReference(), target, p); 
	}

	protected void adoptCase(XModelObject source, XModelObject target, Properties p) throws XModelException {
		//TODO provide that source is redirect or render
		source.getModel().changeObjectAttribute(source, ATTR_VIEW_ID, target.getAttributeValue(ATTR_PATH));
	}

	protected void adoptItem(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceObject i = (ReferenceObject)source;
		adoptSeamPage(i.getReference(), target, p); 
	}

	protected void adoptSeamPage(XModelObject source, XModelObject target, Properties p) throws XModelException {
		String path = target.getAttributeValue(ATTR_PATH);
		String n = path;
		if(n.lastIndexOf('.') > 0) n = n.substring(0, n.lastIndexOf('.'));
		if(n.lastIndexOf('/') >= 0) n = n.substring(n.lastIndexOf('/') + 1);
		String suffix = ProcessAdopt.getPageSuffix(source.getModelEntity().getName());
		XModelObject cs = source.getModel().createModelObject(ENT_NAVIGATION_RULE + suffix, null);
		XModelObject redirect = source.getModel().createModelObject("SeamPageRedirect" + suffix, null);
		redirect.setAttributeValue(ATTR_VIEW_ID, path);
		cs.addChild(redirect);
//TODO
//		cs.setAttributeValue(ATT_FROM_OUTCOME, n);
//		int i = 0;
//		while (source.getChildByPath(cs.getPathPart()) != null)
//		  cs.setAttributeValue(ATT_FROM_OUTCOME, n + (++i));
		DefaultCreateHandler.addCreatedObject(source, cs, p);
	}

	void moveOutput(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceObject i = (ReferenceObject)source;
		moveCase(i.getReference(), target, p); 
	}

	void moveCase(XModelObject source, XModelObject target, Properties p) throws XModelException {
		XModelObject rule = null;
		if(target instanceof ReferenceObject) {
			rule = ((ReferenceObject)target).getReference();
			if(rule == null) {
//				JSFNavigationModel nm = (JSFNavigationModel)JSFProcessStructureHelper.instance.getParentFile(target);
//				String path = target.getAttributeValue(ATT_PATH);
//				rule = nm.addRule(path);
			}
		} else {
			rule = target;
		}
		if(rule == null || source.getParent() == rule) return;
		DefaultRemoveHandler.removeFromParent(source);
		DefaultCreateHandler.addCreatedObject(rule, source, p);
	}
}
