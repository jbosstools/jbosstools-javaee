package org.jboss.beans.test06;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

/**
 * Test 06-2.
 * Sources contain simple bean class MyBean1
 * with injection point of type MyType1.
 * class MyType1 has no bean constructor.
 * Seam config xml contains declaration:
 * <test06:MyType1>
 *  <s:Produces/>
 *  <test06:MyQualifier>two</test06:MyQualifier>
 *  <s:value>
 *    <test06:MyType1>
 *      <s:parameters>
 *        <s:String>
 *          <test06:MyQualifier>one</test06:MyQualifier>
 *        </s:String>
 *      </s:parameters>
 *    </test06:MyType1>
 *  </s:value>
 * </test06:MyType1>
 * 
 * ASSERT: Model contains 1 bean with type MyType1 and qualifier MyQualifier.
 * ASSERT: Qualifier has value member equal to "two".
 * ASSERT: Injection point field 'two' in MyBean1 is resolved to that bean.
 * ASSERT: Injection point field 'one' in MyBean1 is resolved to 2 beans.
 * ASSERT: One of them is the above-mentioned MyType1 bean.
 * ASSERT: The other of them is a bean with type MyType1 InlineBeanQualifier qualifier.
 * 
 * Test 06-3.
 * Uses sources of tests 06-1 and 06-2.
 * 
 * ASSERT: Inner bean of type MyType1 has InlineBeanQualifier qualifier.
 * ASSERT: The inner bean has one injection point.
 * ASSERT: The injection point is resolved to a bean (created in test 06-1).
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean2 {

	@Inject
	@Any
	MyType1 one;

	@Inject
	@MyQualifier("two")
	MyType1 two;

}
