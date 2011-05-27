package org.jboss.generic;

import org.jboss.seam.solder.bean.generic.GenericType;

@GenericType(MyConfiguration.class)
public @interface MyGenericType {
	String value();
}
