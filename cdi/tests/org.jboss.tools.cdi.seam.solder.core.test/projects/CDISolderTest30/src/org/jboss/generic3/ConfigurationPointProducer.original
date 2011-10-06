package org.jboss.generic3;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;

/*
 * Generic configuration points point1 and point2 are not duplicate.
 * This test checks algorithm comparing qualifiers.
 * (First, incorrect version considered point1 a duplicate of point2,
 * if qualifiers of point1 made a subset of qualifiers of point2.)  
 * 
 */
public class ConfigurationPointProducer {
	
	@Produces
	@GenericAnnotation("a")
	@Qualifier1
	Configuration point1;

	@Produces
	@GenericAnnotation("b")
	@Qualifier1
	@Qualifier2
	Configuration point2;

}
