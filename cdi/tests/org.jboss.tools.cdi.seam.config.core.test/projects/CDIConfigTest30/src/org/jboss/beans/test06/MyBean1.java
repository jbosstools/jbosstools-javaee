package org.jboss.beans.test06;

import javax.inject.Inject;

/**
 * Test 06-1.
 * Sources contain simple bean class MyBean1
 * with two injection points of type String.
 * Seam config xml contains declaration:
 * <s:String>
 *  <s:Produces/>
 *  <test06:MyQualifier>one</test06:MyQualifier>
 * </s:String>
 * 
 * ASSERT: Model contains 1 bean with type String and qualifier MyQualifier.
 * ASSERT: Qualifier has value member equal to "one".
 * ASSERT: Injection point field 'one' in MyBean1 is resolved to that bean.
 * ASSERT: Injection point field 'two' in MyBean1 is not resolved to a bean.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean1 {
	@Inject
	@MyQualifier("one")
	String one;

	@Inject
	@MyQualifier("two")
	String two;

}
