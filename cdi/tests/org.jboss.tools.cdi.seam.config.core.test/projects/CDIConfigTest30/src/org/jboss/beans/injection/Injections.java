package org.jboss.beans.injection;

import javax.inject.Inject;

import org.jboss.beans.test04.MyBean5;
import org.jboss.beans.test04.MyQualifier;
import org.jboss.beans.test04.MyType3;
import org.jboss.beans.test05.MyType1;

public class Injections {

	/**
	 * Class bean defined in seam-config.xml
	 */
	@Inject MyBean5 b5;

	/**
	 * Virtual field producer bean defined in seam-config.xml
	 */
	@Inject @org.jboss.beans.test06.MyQualifier("one") String s;
	
	/**
	 * Method producer bean defined in seam-config.xml
	 */
	@Inject @org.jboss.beans.test05.MyQualifier MyType1 t1;
	
	/**
	 * Field producer bean defined in seam-config.xml
	 */
	@Inject @MyQualifier(kind="kind-04-3") MyType3 t3;

}
