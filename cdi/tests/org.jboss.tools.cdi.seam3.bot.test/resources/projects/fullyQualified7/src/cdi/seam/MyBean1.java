package cdi.seam;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.solder.core.FullyQualified;

public class MyBean1 {

	@FullyQualified
	@Produces @Q1 @Named
	public static final MyBean1 uniqueBean = new MyBean1(); 
	
}
