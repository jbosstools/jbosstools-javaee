package org.jboss.generic;

import org.jboss.solder.bean.generic.GenericType;

@GenericType(MyGenericBean.class)
public @interface BrokenGenericType {
	boolean value();
}
