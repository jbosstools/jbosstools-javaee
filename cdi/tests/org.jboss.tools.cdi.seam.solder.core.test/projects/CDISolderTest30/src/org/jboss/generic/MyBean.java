package org.jboss.generic;

/**
 * Beans of this type are produced by MyGenericBean.createMyFirstBean()
 * for every of 5 configurations.
 * It is injected 
 * - into fields first1, first2, first3, first4, first5 of MyBeanInjections
 * to check each configuration.
 * - into generic injection field 'c' of MyGenericField2.
 * - into generic parameter of injection method setMyBean
 * - into type parameter of Event typed fields event1 and event2 of MyBeanInjections.
 */
public class MyBean {
	
	public MyBean(String s) {}

}
