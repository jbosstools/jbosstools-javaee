package org.jboss.jsr299.tck.tests.definition.qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Qualifier;

@BeanWithInnerQualifier.InnerQualifier
public class BeanWithInnerQualifier {
	
	public BeanWithInnerQualifier() {}
	
	@Inject
	@InnerQualifier
	BeanWithInnerQualifier a;

	@Qualifier
	@Target({ TYPE, METHOD, PARAMETER, FIELD })
	@Retention(RUNTIME)
	public @interface InnerQualifier {		
	}

}
