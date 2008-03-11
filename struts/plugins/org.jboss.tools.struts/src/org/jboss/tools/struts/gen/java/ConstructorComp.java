/*
 * ConstructorComp.java
 *
 * Created on March 14, 2003, 6:24 PM
 */

package org.jboss.tools.struts.gen.java;


/**
 *
 * @author  valera
 */
public class ConstructorComp implements java.util.Comparator {
    
    /** Creates a new instance of ConstructorComp */
    public ConstructorComp() {
    }
    
    public int compare(Object o1, Object o2) {
        ConstructorDesc c1 = (ConstructorDesc)o1;
        ConstructorDesc c2 = (ConstructorDesc)o2;
        ParameterList p1 = c1.getParameterList();
        ParameterList p2 = c2.getParameterList();
        int res = p1.size() - p2.size();
        if (res != 0) return res;
        for (int i = 0; i < p1.size(); i++) {
            res = p1.getType(i).compareTo(p2.getType(i));
            if (res != 0) return res;
        }
        return 0;
    }
    
}
