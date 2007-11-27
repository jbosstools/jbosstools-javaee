/*
 * ConstructorDesc.java
 *
 * Created on March 14, 2003, 10:53 AM
 */

package org.jboss.tools.struts.gen.java;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 *
 * @author  valera
 */
public class ConstructorDesc {
    
    protected ParameterList params = new ParameterList();
    protected Set<String> exceptions = new TreeSet<String>();
    protected String body;
    protected int modifier;
    protected String comment;
    
    /** Creates a new instance of ConstructorDesc */
    public ConstructorDesc() {
    }
    
    public ConstructorDesc(String[] names, String[] types) {
        if (names != null) {
            params.addAll(names, types);
        }
    }
    
    public ConstructorDesc(Constructor cons) {
        Class[] types = cons.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            String type = TypeUtil.getTypeName(types[i].getName());
            if (type.startsWith("java.lang.")) type = type.substring(10);
            int ind = type.lastIndexOf('.');
            String name = type.substring(ind + 1, ind + 2).toLowerCase();
            params.add(name+(i+1), type);
        }
        types = cons.getExceptionTypes();
        for (int i = 0; i < types.length; i++) {
            String type = TypeUtil.getTypeName(types[i].getName());
            if (type.startsWith("java.lang.")) type = type.substring(10);
            exceptions.add(type);
        }
        modifier = cons.getModifiers();
    }
    
    public ParameterList getParameterList() {
        return params;
    }
    
    public Set getExceptions() {
        return exceptions;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public int getModifier() {
        return modifier;
    }
    
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ConstructorDesc)) return false;
        ConstructorDesc c = (ConstructorDesc)o;
        return params.equals(c.params);
    }
}
