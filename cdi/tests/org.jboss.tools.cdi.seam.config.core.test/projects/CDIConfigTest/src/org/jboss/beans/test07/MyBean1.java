package org.jboss.beans.test01;

import javax.inject.Named;
/**
 * Test 07-1.
 * Sources contain simple bean class MyBean1 with qualifier @Named("test07-1-a").
 * Seam config xml in a dependent project contains declaration:
 * <test07:MyBean1>
 *  <s:modifies/>
 *  <s:Named>test07-1-b</s:Named>
 * </test07:MyBean1>
 * 
 * ASSERT: Model contains 1 named bean with name "test07-1-a".
 * ASSERT: Model contains no named bean with name "test07-1-b".
 * ASSERT: Model of dependent project contains no named bean with name "test07-1-a".
 * ASSERT: Model of dependent project contains 1 named bean with name "test07-1-b".
 *
 * @author Viacheslav Kabanovich
 *
 */
@Named("test07-1-a")
public class MyBean1 {

}
