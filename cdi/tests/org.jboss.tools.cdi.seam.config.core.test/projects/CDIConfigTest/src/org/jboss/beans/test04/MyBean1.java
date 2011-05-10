package org.jboss.beans.test04;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Test 04-1.
 * Sources contain class MyBean1 that declares producer field of type MyType1,
 * class MyType1 has no bean constructor.
 * Seam config xml contains declaration:
 * <test04:MyBean1>
 *  <s:modifies/>
 * </test04:MyBean1>
 * 
 * ASSERT: Model contains 1 bean with type MyType1.
 * ASSERT: That bean is field producer.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean1 {
	
	@Produces
	public MyType1 myType1 = new MyType1("");

}
