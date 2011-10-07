package org.jboss.beans.test05;

/**
 * Test 05-1
 * Sources contain class MyBean1 that declares method createType 
 * with parameter MyType1.
 * Seam config xml contains declaration:
 * <test05:MyBean1>
 *  <test05:createType>
 *   <s:Produces/>
 *   <test05:MyQualifier/>
 *   <s:parameters>
 *    <test05:MyType1>
 *    </test05:MyType1>
 *   </s:parameters>
 *  </test05:createType>
 * </test05:MyBean1>
 * 
 * ASSERT: Model contains 1 bean with type MyType1 with qualifier MyQualifier.
 * ASSERT: That bean is method producer.
 * ASSERT: That bean has one injection point; it is parameter.
 * ASSERT: That injection point is resolved to class bean with type MyType1 with default qualifier.
 * 
 * @author Viacheslav Kababovich
 *
 */
public class MyBean1 {

	public MyType1 createType(MyType1 template) {
		MyType1 result = new MyType1();
		//use template to modify result.
		return result;
	}

}
