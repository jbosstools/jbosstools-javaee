/*
 * StrutsLinkHelper.java
 *
 * Created on February 26, 2003, 3:47 PM
 */

package org.jboss.tools.struts.model.helpers;

import org.jboss.tools.struts.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.undo.*;
import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.struts.model.handlers.StrutsCopyHandler;
import org.jboss.tools.struts.model.helpers.page.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

/**
 *
 * @author  valera
 */
public class StrutsLinkHelper implements StrutsConstants {
    
    private static final int U_K   = -1;  //UNKNOWN

    // Object types
    private static final int CONFIG    = 0;
    private static final int ACTION    = 1;
    private static final int FORWARD   = 2;
    private static final int EXCEPTION = 3;
    private static final int PAGE      = 4;
    private static final int LINK      = 5;
    private static final int LINK_C    = 6;
    private static final int ACTION_U  = 7;
    private static final int ACTION_F  = 8;
    private static final int ACTION_I  = 9;
    private static final int ACTION_A  = 10;  //ForwardAction
    private static final int ACTION_S  = 11;  //SwitchAction

    // Link types
    private static final int S_P  = 0; //SETPATH
    private static final int S_T  = 1; //SETTARG
    private static final int C_F  = 2; //CRTFORW
    private static final int C_L  = 3; //CRTLINK
    private static final int S_F  = 4; //SETFORW
    private static final int S_I  = 5; //SETINCL
    private static final int R_L  = 6; //RDRLINK redirect link of action
    private static final int R_P  = 7; //RDRPLNK link of page
    private static final int S_M  = 8; //SETPARM


    private static final int[][] LINK_TYPES = new int[][] { //target
       //cfg  act  frw  exc  pag  lnk  l_c  a_u  a_f  a_i, a_a, a_s
        {U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K}, //cfg CONFIG
        {U_K, C_F, S_P, S_P, C_L, R_L, R_P, U_K, S_F, S_I, S_M, U_K}, //act ACTION
        {U_K, U_K, U_K, U_K, C_L, U_K, R_P, U_K, U_K, U_K, U_K, U_K}, //frw FORWARD
        {U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K, U_K}, //exc EXCEPTION
        {U_K, C_F, S_P, S_P, C_L, R_L, R_P, U_K, S_F, S_I, S_M, U_K}  //pag PAGE
    };

    /** Creates a new instance of StrutsLinkHelper */
    public StrutsLinkHelper() {
    }
    
    public boolean canLink(XModelObject target, XModelObject object) {
        if (target == null || object == null) return false;
        XModelObject config = getConfig(target, object);
        if (config == null) return false;
        int objectType = getObjectType(object, true);
        int targetType = getObjectType(target, false);
        if (objectType == U_K || targetType == U_K) return false;
        return LINK_TYPES[targetType][objectType] != U_K;
    }

    public void link(XModelObject target, XModelObject object, java.util.Properties p) {
        if (target == null || object == null) return;
        XModelObject config = getConfig(target, object);
        if (config == null) return;
		WebModulesHelper wmh = WebModulesHelper.getInstance(config.getModel()); 
		String module = wmh.getModuleForConfig(config);
		UrlPattern urlPattern = wmh.getUrlPattern(module);
        
        int objectType = getObjectType(object, true);
        int targetType = getObjectType(target, false);
        if (objectType == U_K || targetType == U_K) return;
        int linkType = LINK_TYPES[targetType][objectType];
        if (linkType == U_K) return;
        String name = p.getProperty(PROP_ORGTARGET, target.getAttributeValue(ATT_NAME));
        String path = target.getAttributeValue(ATT_PATH);
        if (targetType == ACTION || targetType >= ACTION_U) {
			path = urlPattern.getActionUrl(path); // path += ".do";
        }
        if(targetType == FORWARD) {
            path = getForwardName(target);
        }
        switch (linkType) {
            case S_P:
                if(objectType == FORWARD) {
                    path = StrutsProcessStructureHelper.instance.getValidForwardPath(object, path);
                }
                redirect(object, null, path);
                break;
            case S_T:
                redirect(object, name, path);
                break;
            case C_F:
                createForward(object, path);
                break;
            case C_L:
                createLink(object, name, path);
                break;
            case S_F:
                object.getModel().changeObjectAttribute(object, ATT_FORWARD, path);
                break;
            case S_M:
                object.getModel().changeObjectAttribute(object, "parameter", path); //$NON-NLS-1$
                break;
            case S_I:
                object.getModel().changeObjectAttribute(object, ATT_INCLUDE, path);
                break;
            case R_L:
                if(findDuplicateLink(object.getParent(), name, path) != null) {
                    return;
                }
                redirect(object, name, path);
                break;
            case R_P:
                if(findDuplicateLink(object.getParent(), name, path) != null) {
                    return;
                }
                if(TYPE_PAGE.equals(object.getParent().getAttributeValue(ATT_TYPE))) {
                    String attr = null;
                    if(targetType == FORWARD) attr = "forward"; //$NON-NLS-1$
                    else if(urlPattern.isActionUrl(path) /*path.endsWith(".do")*/) attr = "action"; //$NON-NLS-1$
                    redirect_link(object, name, path, attr);
                } else {
                    redirect(object, name, path);
                }
                break;
        }
    }
    
    private void redirect_link(XModelObject object, String target, String path, String attr) {
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("redirect " + DefaultCreateHandler.title(object, false), XTransactionUndo.EDIT); //$NON-NLS-1$
        undo.addUndoable(u);
        long stamp = object.getTimeStamp();
        try {
            boolean isForward = "forward".equals(attr); //$NON-NLS-1$
            if(!new ReplaceConfirmedLinkHelper().replace(object, path, attr)) {
                undo.rollbackTransactionInProgress();
                return;
            }
            if(target != null) object.getModel().changeObjectAttribute(object, ATT_TARGET, target);
            object.getModel().changeObjectAttribute(object, ATT_PATH, ((isForward) ? "" : path)); //$NON-NLS-1$
            String linkShapeAttr = (object.getAttributeValue("link shape") != null) //$NON-NLS-1$
                                   ? "link shape" : ATT_SHAPE; //$NON-NLS-1$
            object.getModel().changeObjectAttribute(object, linkShapeAttr, ""); //$NON-NLS-1$
            if (stamp == object.getTimeStamp()) undo.rollbackTransactionInProgress();
        } catch (RuntimeException e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }

    private void redirect(XModelObject object, String target, String path) {
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("redirect " + DefaultCreateHandler.title(object, false), XTransactionUndo.EDIT); //$NON-NLS-1$
        undo.addUndoable(u);
        long stamp = object.getTimeStamp();
        try {
            String rp = StrutsCopyHandler.referentBuffer;
            if(rp != null) {
                XModelObject r = object.getModel().getByPath(rp);
                if(r != null) {
                    String linkShapeAttr = (r.getAttributeValue("link shape") != null) //$NON-NLS-1$
                                   ? "link shape" : ATT_SHAPE; //$NON-NLS-1$
                    object.getModel().changeObjectAttribute(r, linkShapeAttr, ""); //$NON-NLS-1$
                }
            }
            if(target != null) object.getModel().changeObjectAttribute(object, ATT_TARGET, target);
            object.getModel().changeObjectAttribute(object, ATT_PATH, path);
            if (stamp == object.getTimeStamp()) undo.rollbackTransactionInProgress();
        } catch (RuntimeException e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }

    private XModelObject createLink(XModelObject page, String target, String path) {
        XModelObject link = findDuplicateLink(page, target, path);
        if(link != null) return link;
        String name = StrutsProcessHelper.createName(page, "link"); //$NON-NLS-1$
        String title = path;
        if (title.length() > 0) {
            if (title.charAt(title.length()-1) == '/') title = title.substring(0, title.length()-1);
            title = title.substring(title.lastIndexOf('/')+1);
        }
        Properties props = new Properties();
        props.setProperty(ATT_NAME, name);
        props.setProperty(ATT_TYPE, TYPE_LINK);
        props.setProperty(ATT_PATH, path);
        props.setProperty(ATT_TARGET, target);
        props.setProperty(ATT_TITLE, title);
        link = XModelObjectLoaderUtil.createValidObject(page.getModel(), ENT_PROCESSITEMOUT, props);
        DefaultCreateHandler.addCreatedObject(page, link, -1);
        return link;
    }

    private XModelObject createForward(XModelObject action, String path) {
        String name = path.substring(path.lastIndexOf('/')+1);
        int dot = name.indexOf('.');
        if (dot != -1) name = name.substring(0, dot);
        if (name.length() > 1 && Character.isLowerCase(name.charAt(1))) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        } else if (name.length() == 0) {
            name = "forward"; //$NON-NLS-1$
        }
        if (action.getChildByPath(name) != null) {
            int ind = 2;
            while (action.getChildByPath(name+ind) != null) ind++;
            name += ind;
        }
        String actionEntity = action.getModelEntity().getName();
        String entity = ENT_FORWARD + actionEntity.substring(actionEntity.length()-2);
        Properties props = new Properties();
        props.setProperty(ATT_NAME, name);
        props.setProperty(ATT_PATH, path);
        XModelObject forward = XModelObjectLoaderUtil.createValidObject(action.getModel(), entity, props);
        DefaultCreateHandler.addCreatedObject(action, forward, -1);
        return forward;
    }

    private int getObjectType(XModelObject object, boolean source) {
        String entity = object.getModelEntity().getName();
        if (entity.startsWith(ENT_FORWARD)) {
            if(!source && !object.getParent().getModelEntity().getName().startsWith("StrutsGlobalForwards")) //$NON-NLS-1$
              return CONFIG; /// to forbid drop
            return FORWARD;
        } else if (entity.startsWith(ENT_ACTION)) {
            if(!source) return ACTION;
            String jtype = object.getAttributeValue(ATT_TYPE);
            if("org.apache.struts.actions.SwitchAction".equals(jtype)) return ACTION_S; //$NON-NLS-1$
            if("org.apache.struts.actions.ForwardAction".equals(jtype)) return ACTION_A; //$NON-NLS-1$
            if (object.getAttributeValue(ATT_FORWARD).length() > 0) return ACTION_F;
            if (object.getAttributeValue(ATT_INCLUDE).length() > 0) return ACTION_I;
            return ACTION;
        } else if (entity.startsWith(ENT_EXCEPTION)) {
            return EXCEPTION;
        } else if (entity.startsWith(ENT_PROCESSITEM)) {
            String type = object.getAttributeValue(ATT_TYPE);
            if (TYPE_ACTION.equals(type)) {
                if(!source) return ACTION;
                String subtype = object.getAttributeValue(ATT_SUBTYPE);
                if (SUBTYPE_FORWARD.equals(subtype)) return ACTION_F;
                if (SUBTYPE_INCLUDE.equals(subtype)) return ACTION_I;
                if (SUBTYPE_UNKNOWN.equals(subtype)) return ACTION_U;
                if (SUBTYPE_FORWARDACTION.equals(subtype)) return ACTION_A;
                if (SUBTYPE_SWITCH.equals(subtype)) return ACTION_S;
                return ACTION;
            } else if (TYPE_PAGE.equals(type)) {
                return PAGE;
            } else if (TYPE_LINK.equals(type)) {
                if(!source) return CONFIG;
                String subtype = object.getAttributeValue(ATT_SUBTYPE);
                return subtype.length() == 0 ? LINK : LINK_C;
            } else if (TYPE_FORWARD.equals(type)) {
                if(!source && !object.getModelEntity().getName().equals("StrutsItemOutput")) //$NON-NLS-1$
                  return CONFIG; /// to forbid drop
                return FORWARD;
            } else if (TYPE_EXCEPTION.equals(type)) {
                return EXCEPTION;
            }
            return U_K;
        } else if (entity.startsWith(ENT_STRUTSCONFIG)) {
            return CONFIG;
        }
        return U_K;
    }

    private XModelObject getConfig(XModelObject target, XModelObject object) {
        XModelObject config = target;
        while (!config.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG)) {
            config = config.getParent();
            if (config == null) return null;
        }
        while (object != config) {
            object = object.getParent();
            if (object == null) return null;
        }
        return config;
    }

    private XModelObject findDuplicateLink(XModelObject object, String target, String path) {
        if(!TYPE_PAGE.equals(object.getAttributeValue("type"))) return null; //$NON-NLS-1$
        XModelObject[] cs = object.getChildren();
        for (int i = 0; i < cs.length; i++) {
            if(target.equals(cs[i].getAttributeValue(ATT_TARGET))) {
                if("yes".equals(cs[i].getAttributeValue("shortcut")) || //$NON-NLS-1$ //$NON-NLS-2$
                   "yes".equals(cs[i].getAttributeValue("hidden"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    cs[i].getModel().changeObjectAttribute(cs[i], "shortcut", "no"); //$NON-NLS-1$ //$NON-NLS-2$
                    cs[i].getModel().changeObjectAttribute(cs[i], "hidden", "no"); //$NON-NLS-1$ //$NON-NLS-2$
                    return cs[i];
                }
                ServiceDialog d = object.getModel().getService();
                String mes = NLS.bind(StrutsUIMessages.PAGE_CONTAINS_LINK, object.getAttributeValue("title"), path); //$NON-NLS-1$
                d.showDialog(StrutsUIMessages.ERROR, mes , new String[]{StrutsUIMessages.OK}, null, ServiceDialog.ERROR);
                return cs[i];
            }
        }
        return null;
    }

    private String getForwardName(XModelObject target) {
        if(target instanceof ReferenceObjectImpl) {
            target = ((ReferenceObjectImpl)target).getReference();
        }
        return target.getAttributeValue("name"); //$NON-NLS-1$
    }

}

