/*
 * StrutsAdoptManager.java
 *
 * Created on February 27, 2003, 6:07 PM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.struts.model.handlers.page.create.*;
import java.util.*;

/**
 *
 * @author  valera
 */
public class StrutsAdoptManager implements XAdoptManager, StrutsConstants {

    /** Creates a new instance of StrutsAdoptManager */
    public StrutsAdoptManager() {}

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
        if(isAdoptableJSP(target, object)) {
            adoptJSP(target, object, p);
        } else if(isAdoptableProcessItem(target, object)) {
            adoptProcessItem(target, object, p);
        } else if(isAdoptableTile(target, object)) {
            adoptTile(target, object, p);
        } else if(isAdoptableBundle(object)) {
        	adoptBundle(target, object, p);
        }
    }

    public boolean isAdoptable(XModelObject target, XModelObject object) {
		if(isAdoptableBundle(object)) return true;
        if(isAdoptableJSP(target, object)) return true;
        if(isAdoptableProcessItem(target, object)) return true;
        if(isAdoptableTile(target, object)) return true;
        return false;
    }

    private boolean isAdoptableJSP(XModelObject target, XModelObject object) {
        String entity = object.getModelEntity().getName();
        if (ENT_FILEJSP.equals(entity) || ENT_FILEHTML.equals(entity)) {
            String path = XModelObjectLoaderUtil.getResourcePath(object);
            if (target.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG)) {
                target = target.getChildByPath(ELM_PROCESS);
            }
            return ((StrutsProcessImpl)target).getHelper().getPage(path) == null;
        }
        return false;
    }

    private void adoptJSP(XModelObject target, XModelObject object, java.util.Properties p) {
        if (target.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG)) {
            target = target.getChildByPath(ELM_PROCESS);
        }

        Properties runningProperties = new Properties();
        runningProperties.put("preselectedObject", object);
        if(p != null) runningProperties.putAll(p);
        XActionInvoker.invoke("CreateActions.CreatePage", target, runningProperties);
    }

    private boolean isAdoptableProcessItem(XModelObject target, XModelObject object) {
        if(object == null) return false;
        if("StrutsProcessComment".equals(object.getModelEntity().getName())) {
            return false;
        }
        if (object instanceof ReferenceObjectImpl) {
            if (((ReferenceObjectImpl)object).getReference() == null) return false;
        }
        return target != null && object.isActive() &&
               target.getChildByPath(object.getParent().getPathPart()) != null;
    }

    private void adoptProcessItem(XModelObject target, XModelObject object, java.util.Properties p) {
        target = target.getChildByPath(object.getParent().getPathPart());
        if(target == null) return;
        if("StrutsProcessComment".equals(object.getModelEntity().getName())) {
            return;
        }
        XAction a = object.getModelEntity().getActionList().getAction("CopyActions.Copy");
        try {
        	a.executeHandler(object, p);
        } catch (Exception e) {
        	StrutsModelPlugin.getPluginLog().logError(e);
        }
        a = target.getModelEntity().getActionList().getAction("CopyActions.Paste");
        try {
        	a.executeHandler(target, p);
        } catch (Exception e) {
        	StrutsModelPlugin.getPluginLog().logError(e);
        }
    }

    private boolean isAdoptableTile(XModelObject target, XModelObject object) {
        String entity = object.getModelEntity().getName();
        if ("TilesDefinition".equals(entity)) {
            String path = object.getAttributeValue("name");
            if (target.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG)) {
                target = target.getChildByPath(ELM_PROCESS);
            }
            return ((StrutsProcessImpl)target).getHelper().getPage(path) == null;
        }
        return false;
    }

    private void adoptTile(XModelObject target, XModelObject object, java.util.Properties p) {
        if (target.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG)) {
            target = target.getChildByPath(ELM_PROCESS);
        }
        String path = object.getAttributeValue("name");
        try {
           ((StrutsProcessImpl)target).getHelper().updateTiles();
//           XModelObject tile = 
        	   CreatePageSupport.createPage(target, path, p);
        } catch (Exception e) {}
    }

    protected boolean isAdoptableBundle(XModelObject object) {
        return "FilePROPERTIES".equals(object.getModelEntity().getName());
    }

	public void adoptBundle(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		String res = XModelObjectLoaderUtil.getResourcePath(object);
		if(res == null) return;
		if(res != null && res.endsWith(".properties")) {
			res = res.substring(1, res.length() - 11).replace('/', '.');
		}
		p.setProperty("start text", res);
		p.setProperty("end text", "");
		
	}

}
