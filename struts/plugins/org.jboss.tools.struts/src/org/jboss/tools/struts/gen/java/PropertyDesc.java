/*
 * PropertyDesc.java
 *
 * Created on March 13, 2003, 6:34 PM
 */

package org.jboss.tools.struts.gen.java;

/**
 *
 * @author  valera
 */
public class PropertyDesc {
    
    public String name;
    public String type;
    public int modifier;
    public boolean getter = true;
    public boolean setter = true;
    public String comment;
    public String initial;
    
    /** Creates a new instance of PropertyDesc */
    public PropertyDesc() {
    }
    
    public PropertyDesc(String name, String type, int modifier) {
        this.name = name;
        this.type = type;
        this.modifier = modifier;
    }
    
    public PropertyDesc(String name, String type, int modifier, boolean getter, boolean setter, String comment, String initial) {
        this(name, type, modifier);
        this.getter = getter;
        this.setter = setter;
        this.comment = comment;
        this.initial = initial;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof PropertyDesc)) return false;
        PropertyDesc p = (PropertyDesc)o;
        return name.equals(p.name);
    }
    
}
