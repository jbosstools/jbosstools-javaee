package org.jboss.generic3;

import java.lang.annotation.Retention;

import org.jboss.seam.solder.bean.generic.GenericType;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@GenericType(Configuration.class)
@interface GenericAnnotation {
	String value();
}
