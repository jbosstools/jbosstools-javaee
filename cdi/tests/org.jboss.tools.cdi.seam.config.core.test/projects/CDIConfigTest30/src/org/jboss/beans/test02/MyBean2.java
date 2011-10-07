package org.jboss.beans.test02;

/**
 * Test 02-2.
 * Sources contain simple bean class MyBean2 with qualifier MyQualifier1.
 * Seam config xml contains declaration:
 * <test02:MyBean2>
 *  <s:replaces/>
 *  <test02:MyQualifier2/>
 * </test02:MyBean2>
 * 
 * ASSERT: Model contains no bean with type MyBean2 and qualifier MyQualifier1.
 * ASSERT: Model contains 1 bean with type MyBean2 and qualifier MyQualifier2.
 * 
 * @author Viacheslav Kabanovich
 *
 */
@MyQualifier1
public class MyBean2 {

}
