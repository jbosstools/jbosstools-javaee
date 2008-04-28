/*
 * ActionNameCheck.java
 *
 * Created on July 30, 2003, 12:13 PM
 */

package org.jboss.tools.struts.verification;

import org.jboss.tools.common.verification.vrules.*;

/**
 *
 * @author  valera
 */
public class ActionNameCheck implements VAction {

    private VRule rule;
    
    /** Creates a new instance of ActionNameCheck */
    public ActionNameCheck() {
    }
    
    public VResult[] check(VObject object) {
        VResultFactory factory = rule.getResultFactory();
        String name = (String)object.getAttribute("name");
        if (name.length() == 0) {
            String valid = (String)object.getAttribute("validate");
            if ("yes".equals(valid) || "true".equals(valid)) {
                VResult result = factory.getResult("empty", object,
                    "name", object, "name", new Object[] {object});
                return new VResult[] {result};
            }
        } else {
            VObject bean = object.getParent().getParent()
                .getChild("form-beans").getChild(name);
            if (bean == null) {
                VResult result = factory.getResult("exists", object,
                    "name", object, "name", new Object[] {object, name});
                return new VResult[] {result};
            }
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
