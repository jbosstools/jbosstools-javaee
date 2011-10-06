package org.jboss.generic;

/**
 * Beans of this type are produced by MyGenericBean.myThirdBean
 * for every of 5 configurations.
 * It is injected 
 * - into fields third1, third2, third3, third4, third5 of MyBeanInjections
 * to check each configuration.
 * 
 */
public class MyBean3 {
	
	public MyBean3(String s) {}

}
