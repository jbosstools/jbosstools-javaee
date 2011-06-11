package org.jboss.beans.injection;

import javax.inject.Inject;

import org.jboss.beans.test04.MyQualifier;
import org.jboss.beans.test04.MyType3;

public class Injections {

	@Inject
	@MyQualifier(kind="kind-04-3")
	MyType3 b4;

}
