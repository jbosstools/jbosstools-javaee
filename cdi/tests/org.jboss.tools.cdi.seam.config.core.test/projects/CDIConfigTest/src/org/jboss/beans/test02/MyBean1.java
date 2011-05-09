package org.jboss.beans.test02;

/**
 * Test 02-1.
 * Sources contain simple bean class MyBean1 with qualifier MyQualifier1.
 * Seam config xml contains declaration:
 * <test02:MyBean1>
 *  <s:modifies/>
 *  <test02:MyQualifier2/>
 * </test02:MyBean1>
 * 
 * ASSERT: Model contains 1 bean with type MyBean1 and qualifier MyQualifier1.
 * ASSERT: That bean also has qualifier MyQualifier2.
 * 
 * @author Viacheslav Kabanovich
 *
 */
@MyQualifier1
public class MyBean1 {

}
