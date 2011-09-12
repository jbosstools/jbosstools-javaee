package org.jboss.jsr299.tck.tests.jbt.test.core;

import javax.inject.Inject;

public class TestInjection {

	@Inject @TestQualifier Test i1;   // has @TestQualifier
	@Inject @TestQualifier1 Test i2;  // has @TestQualifier and @TestQualifier1
	@Inject @TestQualifier2 Test i3;  // has @TestQualifier, @TestQualifier1 and @TestQualifier2
	@Inject @TestQualifier3 Test i4;  // @TestQualifier3

	@Inject @TestQualifier3 @TestQualifier2 Test i5;  // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3
	@Inject @TestQualifier2 @TestQualifier3 Test i6;  // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3
	@Inject @TestQualifier Test i7;                   // has @TestQualifier

	@Inject @TestStereotype Test i8;   // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3

	@Inject
	public void foo(@TestQualifier2 @TestQualifier3 Test i8) {  // has @TestQualifier, @TestQualifier1, @TestQualifier2 and @TestQualifier3
	}

	public static class Test {}
}