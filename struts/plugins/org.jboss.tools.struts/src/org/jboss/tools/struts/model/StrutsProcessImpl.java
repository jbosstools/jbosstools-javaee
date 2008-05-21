/*
 * StrutsProcessImpl.java
 *
 * Created on February 24, 2003, 11:25 AM
 */

package org.jboss.tools.struts.model;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.jst.web.model.WebProcess;

/**
 *
 * @author  valera
 */
public class StrutsProcessImpl extends ReferenceObjectImpl implements WebProcess {
	private static final long serialVersionUID = 1701469945093855186L;
    
    protected StrutsProcessHelper phelper = new StrutsProcessHelper(this);
    protected StrutsUpdateHelper uhelper = null;
    protected boolean isPrepared = false;
    
    /** Creates a new instance of StrutsProcessImpl */
    public StrutsProcessImpl() {}
    
    public boolean isPrepared() {
    	return isPrepared;
    }
    
    public void firePrepared() {
    	 isPrepared = true;
    	 fireStructureChanged(3, getPath());
    }
    
	public void autolayout() {
		phelper.autolayout();
	}

    protected void loadChildren() {
        if (isPrepared && reference == null && isActive()) {
            restoreRefs();
            registerListener();
            updateProcess();
        }
    }
    
    protected void restoreRefs() {
        phelper.restoreRefs();
    }
    
    protected void updateProcess() {
        phelper.updateProcess();
    }
    
    protected void registerListener() {
        if (uhelper == null) {
            uhelper = new StrutsUpdateHelper(this);
        }
    }
    
    protected void deactivate() {
        if (uhelper != null) {
            uhelper.unregister();
            uhelper = null;
        }
    }
    
    public StrutsProcessHelper getHelper() {
        return phelper;
    }

	protected void changeTimeStamp() {
		boolean actualBody = false;
		String abts = null;
		XModelObject parent = (XModelObject)getParent();
		if(parent != null) {
			abts = parent.get("actualBodyTimeStamp");
			actualBody = (abts != null && (abts.equals("0") || abts.equals("" + parent.getTimeStamp())));
		}
		super.changeTimeStamp();
		if(actualBody && !abts.equals("0")) {
			parent.set("actualBodyTimeStamp", "" + parent.getTimeStamp());
		}
	}
    
}
