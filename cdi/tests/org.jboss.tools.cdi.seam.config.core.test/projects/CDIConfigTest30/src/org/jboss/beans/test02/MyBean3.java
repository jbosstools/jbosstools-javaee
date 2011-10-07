package org.jboss.beans.test02;

/**
 * Test 02-3.
 * Sources contain simple bean class MyBean3.
 * Seam config xml contains declarations:
 * <test02:MyBean3>
 *  <test02:MyQualifier1/>
 * </test02:MyBean3>
 * <test02:MyBean3>
 *  <test02:MyQualifier2/>
 * </test02:MyBean3>
 * 
 * ASSERT: Model contains 3 bean with type MyBean2.
 * ASSERT: Model contains 1 bean with type MyBean2 and qualifier MyQualifier1.
 * ASSERT: Model contains 1 bean with type MyBean2 and qualifier MyQualifier2.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MyBean3 {

}
