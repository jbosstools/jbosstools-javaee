/*
 * WebProjectTree.java
 *
 * Created on February 12, 2003, 10:40 AM
 */

package org.jboss.tools.struts.webprj.model;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.trees.*;

/**
 *
 * @author  valera
 */
public class WebProjectTree extends DefaultSiftedTree {
    
    /** Creates a new instance of WebProjectTree */
    public WebProjectTree() {
    }
    
	public void dispose() {}

	public XModelObject getRoot() {
        XModelObject[] prjs = model.getRoot().getChildren("WebPrjRoot");
        return prjs.length > 0 ? prjs[0] : null;
    }

}
