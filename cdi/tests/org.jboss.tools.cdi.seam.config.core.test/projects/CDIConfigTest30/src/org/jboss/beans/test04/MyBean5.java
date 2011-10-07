package org.jboss.beans.test04;

import javax.enterprise.inject.Produces;

/**
 * Test 04-5.
 * Sources contain class MyBean5 that declares producer field of type MyType5,
 * class MyType5 has no bean constructor.
 * Seam config xml contains declaration:
 * <test04:MyBean5>
 *  <s:replaces/>
 *  <test04:myType5>
 *   <s:Inject/>
 *   <test04:MyQualifier kind="kind-04-5-a"/>
 *  </test04:myType5>
 * </test04:MyBean5>
 * 
 * ASSERT: Model contains no bean with type MyType5.
 * ASSERT: Model contains 1 bean with type MyBean5.
 * ASSERT: That bean has injection point field with qualifier MyQualifier with kind="kind-04-5-a".
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean5 {
	
	@Produces
	@MyQualifier(kind="kind-04-5")
	public MyType5 myType5 = new MyType5("");
	
}
