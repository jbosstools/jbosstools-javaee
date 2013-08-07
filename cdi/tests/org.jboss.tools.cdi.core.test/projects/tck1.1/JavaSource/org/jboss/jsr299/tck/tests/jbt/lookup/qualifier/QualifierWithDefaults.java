package org.jboss.jsr299.tck.tests.jbt.lookup.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface QualifierWithDefaults {

	String stringValue() default "";
	boolean booleanValue() default false;
	char charValue() default 'c';
	int intValue() default 0;
	long longValue() default 22l;
	short shortValue() default 5;
	float floatValue() default 5.0f;
	double doubleValue() default 3.0d;

	String stringValue2() default "a" + "b";
	boolean booleanValue2() default false & true;
	char charValue2() default (char)7;
	int intValue2() default 1 + 2;
	long longValue2() default 2 + 3;
	short shortValue2() default (short)5;
	float floatValue2() default (float)5.0;
	double doubleValue2() default 3.0d + 1;

}
