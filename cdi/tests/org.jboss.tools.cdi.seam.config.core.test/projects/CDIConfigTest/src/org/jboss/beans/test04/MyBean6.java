package org.jboss.beans.test04;

import javax.inject.Inject;

/**
 * Test 04-6.
 * Sources contain class MyBean6 that declares field of type MyType6,
 * class MyType6 has no bean constructor.
 * Seam config xml contains declaration:
 * <test04:MyBean6>
 *  <test04:MyQualifier kind="kind-04-6"/>
 *  <test04:myType6>
 *   <s:Produces/>
 *  </test04:myType6>
 * </test04:MyBean6>
 * 
	 * ASSERT: Model contains 1 bean with type MyType6.
	 * ASSERT: That bean is field producer.
	 * ASSERT: Model contains 1 bean with type MyBean6 with qualifier MyQualifier
	 * ASSERT: That bean has no injection points.
	 * ASSERT: Model contains 1 bean with type MyBean6 with default qualifier..
	 * ASSERT: That bean has 1 injection point.
	 * ASSERT: That injection point is resolved to bean MyType6.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean6 {
	
	@Inject
	public MyType6 myType6 = new MyType6("");

}
