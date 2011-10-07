package org.jboss.test605;

/**
 * 6.5. Configuring methods
 * Array parameters can be represented using the <s:array> element, 
 * with a child element to represent the type of the array. 
 * E.g. int method(String[] param); could be configured via xml using the following:
<my:method>
    <s:array>
      <my:MethodValueBean/>
    </s:array>
</my:method>
 */
public class MethodBean2 {
	
	public void method(String[] s) {
		
	}

}
