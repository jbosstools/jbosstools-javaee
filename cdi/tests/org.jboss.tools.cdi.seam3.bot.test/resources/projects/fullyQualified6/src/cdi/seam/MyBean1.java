package cdi.seam;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.seam.solder.core.FullyQualified;

public class MyBean1 {

	private static final MyBean1 BEAN = new MyBean1(); 
	
	@FullyQualified
	@Produces @Q1 @Named
	public MyBean1 getUniqueBean() {
		return BEAN;
	}
	
}
