/*
 * TypeUtil.java
 *
 * Created on March 14, 2003, 6:17 PM
 */

package org.jboss.tools.struts.gen.java;

/**
 *
 * @author  valera
 */
public class TypeUtil {
    
    /** Creates a new instance of TypeUtil */
    public TypeUtil() {
    }
    
    // [I -> int
    public static final String getComponentTypeName(String type) {
        if (type == null || type.length() == 0) {
            return null;
        }
        int index = type.lastIndexOf('[');
        if (index >= 0) {
            if (index == type.length()-1 || type.lastIndexOf(']') >= 0) {
                return type.substring(0, index);
            }
            char c = type.charAt(index+1);
            switch (c) {
                case 'Z': return "boolean";
                case 'B': return "byte";
                case 'C': return "char";
                case 'D': return "double";
                case 'F': return "float";
                case 'I': return "int";
                case 'J': return "long";
                case 'S': return "short";
                case 'L': return type.substring(index+2, type.length()-1);
                default: return null; //throw new RuntimeException("Unknown type "+type);
            }
        }
        return type;
    }

    // [I -> int[]
    public static final String getTypeName(String javaName) {
        String typeName = getComponentTypeName(javaName);
        if (typeName != null) {
            for (int i = 0; javaName.charAt(i) == '['; i++) {
                typeName += "[]";
            }
        } else {
            typeName = javaName;
        }
        return typeName;
    }
}
