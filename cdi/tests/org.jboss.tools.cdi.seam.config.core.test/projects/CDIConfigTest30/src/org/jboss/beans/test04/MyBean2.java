package org.jboss.beans.test04;

import javax.enterprise.inject.Produces;

/**
 * Test 04-2.
 * Sources contain class MyBean2 that declares producer field of type MyType2,
 * class MyType2 has no bean constructor.
 * Seam config xml contains declaration:
 * <test04:MyBean2>
 *  <s:modifies/>
 *  <test04:myType2>
 *   <s:Named>test04-2-a</s:Named>
 *  </test04:myType2>
 * </test04:MyBean2>
 * 
 * ASSERT: Model contains 1 bean with type MyType2.
 * ASSERT: That bean is field producer.
 * ASSERT: That bean has qualifier MyQualifier with kind="kind-04-2".
 * ASSERT: That bean has name "test04-2-a".
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean2 {
	
	@Produces
	@MyQualifier(kind="kind-04-2")
	public MyType2 myType2 = new MyType2("");

}
