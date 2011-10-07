package org.jboss.beans.test05;

import javax.inject.Inject;

/**
 * Test 05-2
 * Sources contain class MyBean2 that declares constructor. 
 * Seam config xml contains declaration:
 * <test05:MyBean2>
 *   <s:parameters>
 *    <test05:MyType1>
 *    </test05:MyType1>
 *   </s:parameters>
 * </test05:MyBean2>
 * 
 * ASSERT: Model contains 1 bean with type MyBean2.
 * ASSERT: That bean has one injection point; it is parameter.
 * ASSERT: That injection point is resolved to class bean with type MyType1 with default qualifier.
 * 
 * @author Viacheslav Kababovich
 *
 */
public class MyBean2 {

	public MyBean2(MyType1 type) {
	}

}
