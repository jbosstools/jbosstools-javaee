package org.jboss.generic;

/**
 * Beans of this type are produced by MyGenericBean.createMySecondBean()
 * for every of 5 configurations.
 * It is injected into fields second1, second2, second3 of MyBeanInjections
 * to check some configurations.
 *
 */
public class MyBean2 {
	
	public MyBean2(String s) {}

}
