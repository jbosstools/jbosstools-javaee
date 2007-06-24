/*
 * WebPrjSupport.java
 *
 * Created on February 12, 2003, 11:30 AM
 */

package org.jboss.tools.struts.webprj.model.handlers;

import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.meta.constraint.*;
import org.jboss.tools.common.meta.constraint.impl.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import java.util.*;

/**
 *
 * @author  valera
 */
public abstract class WebPrjSupport extends SpecialWizardSupport {
    
    /** Creates a new instance of WebPrjSupport */
    public WebPrjSupport() {
    }
    
    protected void reset() {
        prepareStep(target, getStepId());
    }
    
    public void action(String name) throws Exception {
        if (NEXT.equals(name)) {
            int step = getStepId();
            try {
                int res = doStep(target, step);
                setStepId(getStepId()+res);
            } finally {
                int next = getStepId();
                if (next != step) {
                    prepareStep(target, next);
                }
            }
        } else if (BACK.equals(name)) {
            int res = undoStep(target, getStepId());
            setStepId(getStepId()+res);
            prepareStep(target, getStepId());
        } else if (CANCEL.equals(name)) {
            //target.getModel().getUndoManager().rollbackTransactionInProgress();
            //u.commit();
            //u = null;
            p.setProperty("canceled", "true");
            setFinished(true);
        } else if (FINISH.equals(name)) {
            //u.commit();
            //u = null;
            int res = doStep(target, getStepId());
            if (res > 0) {
                setFinished(true);
                p.setProperty("finished", "true");
            }
        } else if ("Stop".equals(name)) {
            stopThread(true);
            action(NEXT);
        }
    }

    public abstract String getDescription();
    public abstract Step getStep(int step);

    public String getAttributeMessage(int stepId, String attrname) {
        XAttributeData attr = findAttribute(stepId, attrname);
        XAttributeConstraint cons = attr.getAttribute().getConstraint();
        String msg;
        if (cons instanceof XAttributeConstraintProperties) {
            Properties p = ((XAttributeConstraintProperties)cons).getProperties();
            msg = p.getProperty("description");
        } else {
            msg = Character.toUpperCase(attrname.charAt(0))+attrname.substring(1);
        }
        return msg + (attr.getMandatoryFlag() ? "*" : "");
    }

    protected static void setConstraint(XEntityData data, String attr, List<String> values) {
        XAttributeData ad = HUtil.find(new XEntityData[] {data}, 0, attr);
        XAttribute a = ad.getAttribute();
        XAttributeConstraintAList c = (XAttributeConstraintAList)a.getConstraint();
        String[] vs = values.toArray(new String[values.size()]);
        Arrays.sort(vs);
        c.setValues(vs);
    }
    
    protected void changeAttributeValue(XModelObject object, String name, String value) {
        object.getModel().changeObjectAttribute(object, name, value);
    }
    
    protected XEntityData getEntityData(XModelObject object, int step) {
        return getEntityData()[step];
    }
    
    public String getTitle() {
        String title = super.getTitle();
        if (title.endsWith("...")) title = title.substring(0, title.length()-3);
        return title+" - "+getStep(getStepId()).getTitle();
    }
    
    public String[] getActionNames(int stepId) {
        return getStep(stepId).getActionNames();
    }
    
    public String getMessage(int stepId) {
        return getStep(stepId).getMessage();
    }
    
    public int prepareStep(XModelObject object, int stepId) {
        return getStep(stepId).prepareStep(object);
    }
    
    public int doStep(XModelObject object, int stepId) throws Exception {
        return getStep(stepId).doStep(object);
    }
    
    public int undoStep(XModelObject object, int stepId) {
        return getStep(stepId).undoStep(object);
    }
    
    public interface Step {
        public int prepareStep(XModelObject object);
        public int doStep(XModelObject object) throws Exception;
        public int undoStep(XModelObject object);
        public String getTitle();
        public String[] getActionNames();
        public String getMessage();
    }
    
}
