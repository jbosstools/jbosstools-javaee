package org.jboss.test605;

/**
 * 6.5.Note
 * If a class has a field and a method of the same name then by default the field 
 * will be resolved, unless the element has a child <parameters> element, 
 * in which case it is resolved as a method.
 * In the next example method must be resolved.
<test605:MethodBean3>
    <test605:name>
    	<s:parameters/>
    </test605:name>
</test605:MethodBean3>
 *
 */
public class MethodBean4 {
	String name;

	String name() {
		return "";
	}

}
