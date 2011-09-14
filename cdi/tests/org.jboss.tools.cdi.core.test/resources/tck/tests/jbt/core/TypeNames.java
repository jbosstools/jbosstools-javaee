package org.jboss.jsr299.tck.tests.jbt.core;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class TypeNames {

	@Produces int[] arg1;
	@Produces  long arg2;
	@Produces Integer[] arg3;
	@Produces Short arg4;
	@Produces  Object arg5;

	@Produces
	public int[] getFoo() {
		return new int[]{};
	}

	@Produces
	public long getFoo1() {
		return 0;
	}

	@Produces
	public Integer[] getFoo2() {
		return null;
	}

	@Produces
	public Short getFoo3() {
		return null;
	}

	@Produces
	public Object getFoo4() {
		return null;
	}

	@Inject
	private void injectFoo(int[] arg1, long arg2, Integer[] arg3, Short arg4, Object arg5) {
	}
}