package org.jboss.beans.test03;

import javax.inject.Named;

/**
 * Test 03-3.
 * Sources contain simple bean class MyBean3 with qualifier Named("test03-3-a").
 * Seam config xml contains declarations:
 * <test03:MyBean3>
 *  <s:Named>test03-3-b</s:Named>
 * </test03:MyBean3>
 * <test03:MyBean3>
 *  <s:Named>test03-3-c</s:Named>
 * </test03:MyBean3>
 * 
 * ASSERT: Model contains named beans "test03-3-a", "test03-3-b", "test03-3-c".
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Named("test03-3-a")
public class MyBean3 {

}
