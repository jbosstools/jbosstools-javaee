/*
 * ActionTypeCheck.java
 *
 * Created on July 17, 2003, 11:56 AM
 */

package org.jboss.tools.struts.verification;

import org.jboss.tools.common.verification.vrules.*;

/**
 *
 * @author  valera
 */
public class ActionTypeCheck implements VAction {
    
    private VRule rule;
    
    /** Creates a new instance of ActionTypeCheck */
    public ActionTypeCheck() {
    }

    public VResult[] check(VObject object) {
        String forward = (String)object.getAttribute("forward");
        String include = (String)object.getAttribute("include");
        if (forward.length() > 0 || include.length() > 0) return null;
        String type = (String)object.getAttribute("type");
        VResultFactory factory = rule.getResultFactory();
        String sup = "org.apache.struts.action.Action";
        ValidateTypeUtil tv = new ValidateTypeUtil();
        int tvr = tv.checkClass(object, "type", sup);
        if(tvr == ValidateTypeUtil.EMPTY) {
            VResult result = factory.getResult("empty", object,
                "type", object, "type", new Object[] {object});
            return new VResult[] {result};
        } else if(tvr == ValidateTypeUtil.NOT_FOUND) {
            VResult result = factory.getResult("exists", object,
                "type", object, "type", new Object[] {object, type});
            return new VResult[] {result};
        } else if(tvr == ValidateTypeUtil.WRONG_SUPER) {
            VResult result = factory.getResult("extends", object,
                "type", object, "type", new Object[] {object, type, sup});
            return new VResult[] {result};
        } else if(tvr == ValidateTypeUtil.NOT_UPTODATE) {
            VResult result = factory.getResult("uptodate", object,
                "type", tv.src, null, new Object[] {object, type});
            return new VResult[] {result};
        }
        return null;
    }

    public VRule getRule() {
        return rule;
    }
    
    public void setRule(VRule rule) {
        this.rule = rule;
    }
    
}
