package org.jboss.jsr299.tck.tests.jbt.resolution;

import javax.inject.Inject;

import org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Result;

public class AssignabilityOfRawAndParameterizedTypes<T1 extends Exception, T2 extends Exception, T3> {

	private @Inject Result<? extends Throwable, ? super Exception> injection;
	
	private @Inject Result<? extends Exception, ? super Exception> injection2;

	private @Inject Result<Exception, Exception> injection3;

	private @Inject Result<T1, T2> injection4;

	private @Inject Result2<?> injection5;
	
	private @Inject Result2<Object> injection6;
	
	private @Inject Result2<T3> injection7;
	
}
