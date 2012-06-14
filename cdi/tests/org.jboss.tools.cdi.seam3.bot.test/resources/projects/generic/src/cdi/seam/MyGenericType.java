package cdi.seam;

import org.jboss.solder.bean.generic.GenericType;

@GenericType(MyConfiguration.class)
public @interface MyGenericType {
	String value();
}