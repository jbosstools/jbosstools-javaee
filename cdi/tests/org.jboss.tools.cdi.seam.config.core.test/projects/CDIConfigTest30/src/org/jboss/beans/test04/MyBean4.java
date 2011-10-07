package org.jboss.beans.test04;

import javax.enterprise.inject.Produces;

/**
 * Test 04-4.
 * Sources contain class MyBean4 that declares producer field of type MyType4,
 * class MyType4 has no bean constructor.
 * Seam config xml contains declaration:
 * <test04:MyBean4>
 *  <s:replaces/>
 * </test04:MyBean4>
 * 
 * ASSERT: Model contains no bean with type MyType4.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean4 {
	
	@Produces
	public MyType4 myType4 = new MyType4("");

}
