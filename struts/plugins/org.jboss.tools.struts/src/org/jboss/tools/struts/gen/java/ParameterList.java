/*
 * ParameterList.java
 *
 * Created on March 14, 2003, 10:42 AM
 */

package org.jboss.tools.struts.gen.java;

import java.util.*;

/**
 *
 * @author  valera
 */
public class ParameterList {
    
    private List<String> names = new ArrayList<String>();
    private List<String> types = new ArrayList<String>();
    
    /** Creates a new instance of ParameterList */
    public ParameterList() {
    }
    
    public ParameterList(Collection<String> names, Collection<String> types) {
        addAll(names, types);
    }

    public ParameterList(String[] names, String[] types) {
        addAll(names, types);
    }

    public String getName(int index) {
        return (String)names.get(index);
    }
    
    public String getType(int index) {
        return (String)types.get(index);
    }
    
    public void add(String name, String type) {
        names.add(name);
        types.add(type);
    }
    
    public void addAll(Collection<String> names, Collection<String> types) {
        this.names.addAll(names);
        this.types.addAll(types);
    }

    public void addAll(String[] names, String[] types) {
        addAll(Arrays.asList(names), Arrays.asList(types));
    }

    public void set(String name, String type, int index) {
        names.set(index, name);
        types.set(index, type);
    }
    
    public void insert(String name, String type, int index) {
        names.add(index, name);
        types.add(index, type);
    }
    
    public void remove(int index) {
        names.remove(index);
        types.remove(index);
    }
    
    public int size() {
        return names.size();
    }
    
    public void clear() {
        names.clear();
        types.clear();
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ParameterList)) return false;
        ParameterList p = (ParameterList)o;
        return types.equals(p.types);
    }
}
