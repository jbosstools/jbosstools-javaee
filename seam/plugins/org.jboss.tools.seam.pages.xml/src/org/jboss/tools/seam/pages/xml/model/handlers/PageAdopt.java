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
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class PageAdopt implements XAdoptManager, SeamPagesConstants {
	
	public static boolean move_case = false;

	public boolean isAdoptable(XModelObject target, XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(ENT_DIAGRAM_ITEM_OUTPUT.equals(entity)) {
			if(move_case) {
				return canMoveCase(target, object);
			}
			return canBeOutputTarget(target);
		} 
		if(ENT_DIAGRAM_ITEM.equals(entity)) {
			return canDrawCustomLink(target, object) || canBeOutputTarget(target);
		}
		if(entity.startsWith(ENT_SEAM_PAGE)) {
			return canBeOutputTarget(target);
		}
		return false;
	}
	
	private boolean canBeOutputTarget(XModelObject group) {
		String type = group.getAttributeValue(ATTR_TYPE);
		if(TYPE_EXCEPTION.equals(type)) {
			//Exception cannot be the target
			return false;
		}
		String path = group.getAttributeValue(ATTR_PATH);
		if(path == null) path = group.getAttributeValue(ATTR_VIEW_ID);
		if(path == null) return false;
		if(path.length() == 0 || path.indexOf("*") >= 0) return false;
		return true;
	}

	private boolean canDrawCustomLink(XModelObject target, XModelObject source) {
		if(!(source instanceof ReferenceObject)) {
			return false;
		}
		ReferenceObject i = (ReferenceObject)source;
		if(i.getReference() != null) {
			//only virtual items can be sources of custom links.
			return false;
		}
		String sourcePath = source.getAttributeValue(ATTR_PATH);
		if(!isEL(sourcePath)) {
			return false;
		}

		if(!(target instanceof ReferenceObject)) {
			return false;
		}
		ReferenceObject j = (ReferenceObject)target;
		if(j.getReference() == null) {
			//only real items can be targets of custom links
			return false;
		}

		String type = target.getAttributeValue(ATTR_TYPE);
		if(TYPE_EXCEPTION.equals(type)) {
			//Exception cannot be the target
			return false;
		}
		String path = target.getAttributeValue(ATTR_PATH);
		if(path == null) path = target.getAttributeValue(ATTR_VIEW_ID);
		if(path == null) return false;

		return true;
	}

	public static boolean isEL(String path) {
		if(path.indexOf('{') < 0) {
			return false;
		}
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
		if(case_ == null || case_.getParent().getModelEntity().getName().startsWith(ENT_EXCEPTION)) {
			return false;
		}
		return (rule == null || !case_.getPath().startsWith(rule.getPath()));
	}

	public void adopt(XModelObject target, XModelObject object, Properties p) throws XModelException {
		String entity = object.getModelEntity().getName();
		if(ENT_DIAGRAM_ITEM_OUTPUT.equals(entity)) {
			if(move_case) {
				moveOutput(object, target, p);
			} else {
				adoptOutput(object, target, p);
			}
		} else if(ENT_DIAGRAM_ITEM.equals(entity)) {
			if(canDrawCustomLink(target, object)) {
				drawCustomLink(target, object);
			} else {
				adoptItem(object, target, p);
			}
		} else if(entity.startsWith(ENT_SEAM_PAGE)) {
			adoptSeamPage(object, target, p);
		}
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
		if(i.getReference() == null) {
			String path = i.getAttributeValue(ATTR_PATH);
			if(isEL(path)) {
				//do not create rule for EL.
				return;
			}
			XModelObject rule = createRule(target, path);
			adoptSeamPage(rule, target, p);
		} else {
			adoptSeamPage(i.getReference(), target, p);
		}
	}

	protected void adoptSeamPage(XModelObject source, XModelObject target, Properties p) throws XModelException {
		String path = target.getAttributeValue(ATTR_PATH);
		if(source != null && source.getModelEntity().getName().startsWith(ENT_EXCEPTION)) {
			XModelObject redirect = source.getChildByPath("target");
			if(redirect != null) {
				redirect.getModel().editObjectAttribute(redirect, ATTR_VIEW_ID, path);
				return;
			}
		}
//		String n = path;
//		if(n.lastIndexOf('.') > 0) n = n.substring(0, n.lastIndexOf('.'));
//		if(n.lastIndexOf('/') >= 0) n = n.substring(n.lastIndexOf('/') + 1);

		XModelObject cs = source.getModel().createModelObject(XModelObjectUtil.getVersionedChildEntity(source.getModelEntity(), ENT_NAVIGATION_RULE), null);
		XModelObject redirect = source.getModel().createModelObject(XModelObjectUtil.getVersionedChildEntity(cs.getModelEntity(), "SeamPageRedirect"), null);
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
				String path = target.getAttributeValue(ATTR_PATH);
				rule = createRule(target, path);
			}
		} else {
			rule = target;
		}
		if(rule == null || source.getParent() == rule) return;
		source = source.getParent();
		if(source.getModelEntity().getName().startsWith(ENT_RULE)) {
			//no!
			source = source.getParent();
		}
		DefaultRemoveHandler.removeFromParent(source);
		DefaultCreateHandler.addCreatedObject(rule, source, p);
	}

	XModelObject createRule(XModelObject o, String path) throws XModelException {
		XModelObject file = SeamPagesDiagramStructureHelper.instance.getParentFile(o);
		XModelObject pages = file.getChildByPath(FOLDER_PAGES);
		String childEntity = pages.getModelEntity().getChildren()[0].getName();
		XModelObject rule = pages.getModel().createModelObject(childEntity, null);
		rule.setAttributeValue(ATTR_VIEW_ID, path);
		DefaultCreateHandler.addCreatedObject(pages, rule, FindObjectHelper.IN_EDITOR_ONLY);
		return rule;
	}

	void drawCustomLink(XModelObject target, XModelObject source) throws XModelException {
		ReferenceObject j = (ReferenceObject)target;
		String viewId = j.getReference().getAttributeValue(ATTR_VIEW_ID);
		XModelObject output = target.getModel().createModelObject(ENT_DIAGRAM_ITEM_OUTPUT, null);
		output.setAttributeValue(ATTR_ID, viewId);
		output.setAttributeValue(ATTR_PATH, viewId);
		String name = XModelObjectUtil.createNewChildName("output", source);
		output.setAttributeValue(ATTR_NAME, name);
		output.setAttributeValue(ATTR_SUBTYPE, SUBTYPE_CUSTOM);
		output.setAttributeValue(ATTR_TARGET, target.getPathPart());
		DefaultCreateHandler.addCreatedObject(source, output, FindObjectHelper.IN_EDITOR_ONLY);
	}

}
