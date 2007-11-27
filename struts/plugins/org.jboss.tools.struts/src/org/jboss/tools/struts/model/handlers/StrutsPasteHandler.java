/*
 * StrutsPasteHandler.java
 *
 * Created on February 21, 2003, 5:55 PM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.XModelBuffer;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.util.*;
import java.util.Properties;
import org.jboss.tools.struts.model.helpers.*;

/**
 *
 * @author  valera
 */
public class StrutsPasteHandler extends PasteHandler {

    public StrutsPasteHandler() {}

    Properties _p = null;

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        if (prop == null) prop = new Properties();
        _p = prop;
        if("true".equals(prop.getProperty("secondPass"))) {
            super.executeHandler(object, prop);
            return;
        }

        XModelObject o = object.getModel().getModelBuffer().source();
        if(o != null && "StrutsProcessComment".equals(o.getModelEntity().getName())) {
            String n = XModelObjectUtil.createNewChildName("comment", object);
            object.getModel().getModelBuffer().copy().setAttributeValue("name", n);
            super.executeHandler(object, prop);
            autolayoutIfNecessary(object);
            return;
        }

        prop.setProperty(StrutsConstants.PROP_ORGTARGET, object.getPathPart());
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ref != null) object = ref;
        }
        
        if(isSourceAction(object) ||
           !differentStrutsFiles(object, object.getModel().getModelBuffer().source()) ||
           isPasteToNavigator(prop)) {
            super.executeHandler(object, prop);
        }
        autolayoutIfNecessary(object);
        _p = null;
    }
    
    private void autolayoutIfNecessary(XModelObject object) {
        if(_p != null && "true".equals(_p.getProperty("autolayout"))) {
        	XModelObject process = StrutsProcessStructureHelper.instance.getProcess(object);
        	StrutsProcessStructureHelper.instance.autolayout(process);
        }        
    }

    private boolean isSourceAction(XModelObject object) {
        return object.getModel().getModelBuffer().source().getModelEntity().getName().startsWith("StrutsAction");
    }

    private boolean isPasteToNavigator(Properties p) {
        return (p != null && "navigator".equals(p.getProperty("actionSourceGUIComponentID")));
    }

    private boolean differentStrutsFiles(XModelObject o1, XModelObject o2) {
        while(o1 != null && o1.getFileType() == XFileObject.NONE) o1 = o1.getParent();
        while(o2 != null && o2.getFileType() == XFileObject.NONE) o2 = o2.getParent();
        if(o1 == null || o2 == null) return false;
        if(!o1.getModelEntity().getName().startsWith(StrutsConstants.ENT_STRUTSCONFIG)) return false;
        if(!o2.getModelEntity().getName().startsWith(StrutsConstants.ENT_STRUTSCONFIG)) return false;
        return (o1 != o2);
    }

    protected XModelObject modify(XModelObject c) {
    	if(_p == null || !_p.containsKey("process.mouse.x")) {
    		if(_p == null) _p = new Properties();
    		_p.setProperty("autolayout", "true");
    		CreateConfigElementHandler.setItemShape(c, "");
    	} else {
    		CreateConfigElementHandler.setItemShape(c, _p);
    	}
        return c;
    }

    public boolean isEnabled(XModelObject object) {
    	if(isChildPageOfProcess(object)) return false;
        if(super.isEnabled(object)) return true;
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ref != null) return super.isEnabled(ref);
        }
        return false;
    }


    protected void pasteOnDrop(XModelObject parent, int sourceIndex, Properties p) throws Exception {
        XModelObject o = parent.getModel().getModelBuffer().source(sourceIndex);
        String gui = p.getProperty("actionSourceGUIComponentID");
        String entity = o.getModelEntity().getName();
        if("editor".equals(gui) && entity.startsWith("StrutsAction") && o.getParent() == parent) {
            if(o.getModelEntity().getActionList().getAction("CreateActions.CreateForward") != null) {
                XActionInvoker.invoke("CreateActions.CreateForward", o, p);
            }
        } else if("editor".equals(gui) && insertReference(parent, entity, o, p)) {
            return;
        } else if ("editor".equals(gui) && o.getParent() == parent
                   && parent.getModelEntity().getName().startsWith("StrutsGlobal")) {
            XActionInvoker.invoke("EditPath", o, p);
        } else if (isPasteToNavigator(p) && isParent(parent, o)) {
			super.pasteOnDrop(parent, sourceIndex, p);
        } else if (canAdopt(parent, o)) {
            drop(parent, o, p);
        } else if("StrutsProcessComment".equals(o.getModelEntity().getName())) {
            super.pasteOnDrop(parent.getChildByPath("process"), sourceIndex, p);
        } else {
            super.pasteOnDrop(parent, sourceIndex, p);
        }
    }

    /*
     * If action 1.1 is dropped to another config, unconfirmed item
     * is created on its process.
     */
    
    static String ENT_ACTION_11 = StrutsConstants.ENT_ACTION + StrutsConstants.VER_SUFFIX_11;
    static String ENT_ACTION_12 = StrutsConstants.ENT_ACTION + StrutsConstants.VER_SUFFIX_12;

    protected boolean insertReference(XModelObject parent, String entity, XModelObject o, Properties p) {
        if((!entity.equals(ENT_ACTION_11) && !entity.equals(ENT_ACTION_12))
           || o.getParent() == parent) return false;
        XModelObject process = StrutsProcessStructureHelper.instance.getProcess(parent);
        StrutsProcessHelper h = StrutsProcessHelper.getHelper(process);
        String path = o.getAttributeValue(StrutsConstants.ATT_PATH);
        String module = StrutsProcessStructureHelper.instance.getProcessModule(o);
        if(module.length() == 0) path = "/" + path; 
        else path = StrutsProcessStructureHelper.instance.getUrlPattern(process).getContextRelativePath(path, module); //module + path;
        if(h.getAction(path) != null) return true;
        Properties q = new Properties();
        q.setProperty(StrutsConstants.ATT_NAME, StrutsProcessHelper.createName(process, "action"));
        q.setProperty(StrutsConstants.ATT_TYPE, StrutsConstants.TYPE_ACTION);
        q.setProperty(StrutsConstants.ATT_SUBTYPE, StrutsConstants.SUBTYPE_UNKNOWN);
        q.setProperty(StrutsConstants.ATT_PATH, path);
        XModelObject action = parent.getModel().createModelObject(StrutsConstants.ENT_PROCESSITEM, q);
        CreateConfigElementHandler.setItemShape(action, p);
        DefaultCreateHandler.addCreatedObject(process, action, p);
        h.registerAction(action);
        return true;
    }
    
    private boolean isChildPageOfProcess(XModelObject object) {
    	if(object == null) return false;
    	if(!"StrutsProcess".equals(object.getModelEntity().getName())) return false;
        XModelBuffer buffer = object.getModel().getModelBuffer();
        int bs = buffer.getSize();
        if(bs == 0 || object == null || !object.isObjectEditable()) return false;
        for (int i = 0; i < bs; i++) {
            XModelObject s = buffer.source(i);
            boolean b = (s != null && (isParent(object, s)));
            if(!b) continue;
            String type = s.getAttributeValue("type");
            if("page".equals(type)) return true;
        }    	
    	return false;
    }

}
