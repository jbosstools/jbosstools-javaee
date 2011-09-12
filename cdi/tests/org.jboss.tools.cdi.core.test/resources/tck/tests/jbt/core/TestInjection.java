package org.jboss.jsr299.tck.tests.jbt.test.core;

import javax.inject.Inject;

public class TestInjection {

	@Inject @TestQualifier Object i1;   // has @TestQualifier
	@Inject @TestQualifier1 Object i2;  // has @TestQualifier and @TestQualifier1
	@Inject @TestQualifier2 Object i3;  // has @TestQualifier, @TestQualifier1 and @TestQualifier2
	@Inject @TestQualifier3 Object i4;  // @TestQualifier3

	@Inject @TestQualifier3 @TestQualifier2 Object i5;  // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3
	@Inject @TestQualifier2 @TestQualifier3 Object i6;  // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3
	@Inject @TestQualifier Object i7;

	@Inject @TestStereotype Object i8;   // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3

	@Inject
	public void foo(@TestQualifier2 @TestQualifier3 Object i8) {  // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3
	}
}