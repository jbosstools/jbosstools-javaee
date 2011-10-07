package org.jboss.beans.test04;

/**
 * Test 04-3.
 * Sources contain class MyBean2 that declares a field of type MyType3,
 * class MyType3 has no bean constructor.
 * Seam config xml contains declaration:
 * <test04:MyBean3>
 *  <s:modifies/>
 *  <test04:myType3>
 *   <s:Produces/>
 *  </test04:myType3>
 * </test04:MyBean3>
 * 
 * ASSERT: Model contains 1 bean with type MyType3.
 * ASSERT: That bean is field producer.
 * ASSERT: That bean has qualifier MyQualifier with kind="kind-04-3".
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean3 {
	
	@MyQualifier(kind="kind-04-3")
	public MyType3 myType3 = new MyType3("");
	
}
