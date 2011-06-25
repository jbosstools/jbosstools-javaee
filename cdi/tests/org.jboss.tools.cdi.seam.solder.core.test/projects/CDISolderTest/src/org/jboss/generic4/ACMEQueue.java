package org.jboss.generic4;

import java.lang.annotation.Retention;

import org.jboss.seam.solder.bean.generic.GenericType;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@GenericType(MessageSystemConfiguration.class)
@interface ACMEQueue {

	String value();
}
