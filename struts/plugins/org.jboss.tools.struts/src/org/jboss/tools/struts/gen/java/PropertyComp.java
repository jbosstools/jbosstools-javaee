/*
 * PropertyComp.java
 *
 * Created on March 14, 2003, 9:24 AM
 */

package org.jboss.tools.struts.gen.java;

import java.lang.reflect.Modifier;

/**
 *
 * @author  valera
 */
public class PropertyComp implements java.util.Comparator<PropertyDesc> {
    
    /** Creates a new instance of PropertyComp */
    public PropertyComp() {
    }
    
    public int compare(PropertyDesc p1, PropertyDesc p2) {
        int res = getWeight(p1) - getWeight(p2);
        if (res != 0) return res;
        return p1.name.compareTo(p2.name);
    }
    
    private int getWeight(PropertyDesc p) {
        int w = Modifier.isPrivate(p.modifier) ? 4 : 
                Modifier.isProtected(p.modifier) ? 1 :
                Modifier.isPublic(p.modifier) ? 0 : 2;
        w += Modifier.isStatic(p.modifier) ? 0 : 8;
        return w;
    }
    
}
