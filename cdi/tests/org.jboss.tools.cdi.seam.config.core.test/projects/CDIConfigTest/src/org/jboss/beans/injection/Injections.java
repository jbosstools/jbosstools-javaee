package org.jboss.beans.injection;

import javax.inject.Inject;

import org.jboss.beans.test04.MyBean5;
import org.jboss.beans.test04.MyQualifier;
import org.jboss.beans.test04.MyType3;
import org.jboss.beans.test05.MyType1;

public class Injections {

	@Inject MyBean5 b5;

	@Inject @org.jboss.beans.test06.MyQualifier("one") String s;
	
	@Inject @org.jboss.beans.test05.MyQualifier MyType1 t1;
	
	@Inject @MyQualifier(kind="kind-04-3") MyType3 t3;

}
