package org.jboss.beans.test03;

import javax.inject.Named;

/**
 * Test 03-2.
 * Sources contain simple bean class MyBean2 with qualifier Named("test03-2-a").
 * Seam config xml contains declaration:
 * <test03:MyBean2>
 *  <s:replaces/>
 *  <s:Named>test03-2-b</s:Named>
 * </test03:MyBean2>
 * 
 * ASSERT: Model contains no named bean with name "test03-2-a".
 * ASSERT: Model contains 1 named bean with name "test03-2-b".
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Named("test03-2-a")
public class MyBean2 {

}
