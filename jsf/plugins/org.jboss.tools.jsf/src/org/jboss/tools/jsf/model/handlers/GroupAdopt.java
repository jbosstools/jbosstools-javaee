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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.helpers.*;
import org.jboss.tools.jst.web.model.ReferenceObject;

public class GroupAdopt implements XAdoptManager, JSFConstants {
	
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
		if(ENT_PROCESS_GROUP.equals(entity)) {
			return canBeOutputTarget(target);
		}
		if(ENT_NAVIGATION_RULE.equals(entity)) {
			return canBeOutputTarget(target);
		}
		if(ENT_NAVIGATION_CASE.equals(entity)) {
			return canBeOutputTarget(target);
		}
		return false;
	}
	
	private boolean canBeOutputTarget(XModelObject group) {
		String path = group.getAttributeValue("path"); //$NON-NLS-1$
		if(path.length() == 0 || path.indexOf("*") >= 0) return false; //$NON-NLS-1$
		return true;
	}
	
	private boolean canMoveCase(XModelObject target, XModelObject object) {
		XModelObject case_ = null;
		if(object instanceof ReferenceObjectImpl) {
			case_ = ((ReferenceObject)object).getReference();
		} else {
			case_ = object;
		}
		XModelObject rule = null;
		if(target instanceof ReferenceGroupImpl) {
			rule = ((ReferenceGroupImpl)target).getReference();
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
		else if(ENT_PROCESS_GROUP.equals(entity)) adoptGroup(object, target, p);
		else if(ENT_NAVIGATION_RULE.equals(entity)) adoptRule(object, target, p);
		else if(ENT_NAVIGATION_CASE.equals(entity)) adoptCase(object, target, p);
	}
	
	protected void adoptOutput(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceObject i = (ReferenceObject)source;
		adoptCase(i.getReference(), target, p); 
	}

	protected void adoptCase(XModelObject source, XModelObject target, Properties p) throws XModelException {
		source.getModel().changeObjectAttribute(source, ATT_TO_VIEW_ID, target.getAttributeValue(ATT_PATH));
	}

	protected void adoptItem(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceObject i = (ReferenceObject)source;
		adoptRule(i.getReference(), target, p); 
	}

	protected void adoptRule(XModelObject source, XModelObject target, Properties p) throws XModelException {
		String path = target.getAttributeValue(ATT_PATH);
		String n = path;
		if(n.lastIndexOf('.') > 0) n = n.substring(0, n.lastIndexOf('.'));
		if(n.lastIndexOf('/') >= 0) n = n.substring(n.lastIndexOf('/') + 1);
		XModelObject cs = source.getModel().createModelObject(ENT_NAVIGATION_CASE, null);
		cs.setAttributeValue(ATT_TO_VIEW_ID, path);
		cs.setAttributeValue(ATT_FROM_OUTCOME, n);
		int i = 0;
		while (source.getChildByPath(cs.getPathPart()) != null)
		  cs.setAttributeValue(ATT_FROM_OUTCOME, n + (++i));
		DefaultCreateHandler.addCreatedObject(source, cs, p);
	}

	protected void adoptGroup(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceGroupImpl g = (ReferenceGroupImpl)source;
		XModelObject[] os = g.getReferences();
		if(os != null && os.length > 0) {
			adoptRule(os[os.length - 1], target, p);		
		} else {
			JSFNavigationModel nm = (JSFNavigationModel)JSFProcessStructureHelper.instance.getParentFile(target);
			String path = g.getAttributeValue(ATT_PATH);
			int count = nm.getRuleCount(path);
			String pp = AddCaseToGroupHandler.revalidateGroupPath(path, g.getPathPart(), count);
			XModelObject gx = g.getParent().getChildByPath(pp);
			if(gx == null || gx == g) {
				g.setAttributeValue(ATT_NAME, pp);
				g.setAttributeValue("persistent", "false");			 //$NON-NLS-1$ //$NON-NLS-2$
			} else if(gx instanceof ReferenceGroupImpl && ((ReferenceGroupImpl)gx).getReferences().length == 0) {
				String shape = g.getAttributeValue("shape"); //$NON-NLS-1$
				g.setAttributeValue("shape", gx.getAttributeValue("shape")); //$NON-NLS-1$ //$NON-NLS-2$
				gx.setAttributeValue("shape", shape); //$NON-NLS-1$
				gx.setAttributeValue("persistent", "false");			 //$NON-NLS-1$ //$NON-NLS-2$
			}
			adoptRule(nm.addRule(path), target, p);			
		}
	}
	
	void moveOutput(XModelObject source, XModelObject target, Properties p) throws XModelException {
		ReferenceObject i = (ReferenceObject)source;
		moveCase(i.getReference(), target, p); 
	}

	void moveCase(XModelObject source, XModelObject target, Properties p) throws XModelException {
		XModelObject rule = null;
		if(target instanceof ReferenceGroupImpl) {
			rule = ((ReferenceGroupImpl)target).getReference();
			if(rule == null) {
				JSFNavigationModel nm = (JSFNavigationModel)JSFProcessStructureHelper.instance.getParentFile(target);
				String path = target.getAttributeValue(ATT_PATH);
				rule = nm.addRule(path);
			}
		} else {
			rule = target;
		}
		if(rule == null || source.getParent() == rule) return;
		DefaultRemoveHandler.removeFromParent(source);
		DefaultCreateHandler.addCreatedObject(rule, source, p);
	}
}
