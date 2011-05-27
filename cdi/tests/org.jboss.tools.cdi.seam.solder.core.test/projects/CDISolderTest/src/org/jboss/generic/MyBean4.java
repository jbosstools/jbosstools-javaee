package org.jboss.generic;

/**
 * Beans of this type are produced by MyGenericBean2.myFourthBean
 * for every of 5 configurations.
 * It is injected 
 * - into parameters fourth1, fourth2, fourth3, fourth4, fourth5 of 
 * method MyBeanInjections.setAllMyBean4 to check each configuration.
 * 
 */
public class MyBean4 {
	
	public MyBean4(String s) {}

}
