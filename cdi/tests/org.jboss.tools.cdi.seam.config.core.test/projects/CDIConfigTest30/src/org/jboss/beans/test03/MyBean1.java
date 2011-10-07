package org.jboss.beans.test03;

import javax.inject.Named;

/**
 * Test 03-1.
 * Sources contain simple bean class MyBean1 with qualifier Named("test03-1-a").
 * Seam config xml contains declaration:
 * <test03:MyBean1>
 *  <s:modifies/>
 *  <s:Named>test03-1-b</s:Named>
 * </test03:MyBean1>
 * 
 * ASSERT: Model contains no named bean with name "test03-1-a".
 * ASSERT: Model contains 1 named bean with name "test03-1-b".
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Named("test03-1-a")
public class MyBean1 {

}
