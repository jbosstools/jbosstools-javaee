package org.jboss.beans.test06;

import javax.inject.Inject;

/**
 * Test 06-4.
 * 
 * Sources contain simple bean class MyBean3
 * with injection point of type MyInterface
 * Seam config xml contains declaration:
 * <test06:MyInterface>
 *  <s:Produces/>
 *  <s:value>
 *   <test06:MyImpl></test06:MyImpl>
 *  </s:value>
 * </test06:MyInterface>
 * ASSERT: Model contains one bean of type MyInterface with qualifier Default.
 * ASSERT: Injection point field 'i' in MyBean3 is resolved to that bean.
 */
public class MyBean3 {

	@Inject MyInterface i;

}
