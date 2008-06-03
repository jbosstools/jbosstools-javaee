/*
 * MethodDesc.java
 *
 * Created on March 14, 2003, 11:07 AM
 */

package org.jboss.tools.struts.gen.java;

/**
 *
 * @author  valera
 */
public class MethodDesc extends ConstructorDesc {
    
    protected String name;
    protected String type;
    
    /** Creates a new instance of MethodDesc */
    public MethodDesc() {
    }
    
    public MethodDesc(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof MethodDesc)) return false;
        MethodDesc m = (MethodDesc)o;
        return name.equals(m.name) && super.equals(m);
    }
}
