package org.jboss.jsr299.tck.tests.jbt.lookup.duplicateName;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

public class TestNamed {

	@Inject	String s; // Ambiguous
	@Inject	@Named String foo3; // OK
	@Inject	@Named("foo3") String s1; // OK

	@Inject	@Named String xyz; // OK
	@Inject	@Named("xyz") String s2; // OK

	@Inject	@Named String abc; // OK
	@Inject	@Named("abc") String s3; // OK

	@Inject	@Named String foo; // OK
	@Inject	@Named("foo") String s4; // OK

	@Inject	@Named String unknownName; // Unsatisfied
	@Inject	@Named("unknownName") String unknownName1; // Unsatisfied

	@Inject	public void doSmth(@Named("foo4") String foo8) {} // Ambiguous: foo4, foo4(), foo5(), foo6()
	@Inject	@Named("foo4") String s5;  // Ambiguous: foo4, foo4(), foo5(), foo6()

	@Produces @TestS // OK
	public String foo() {return "";}

	@Produces @TestS @Named("abc") // OK
	public String foo1;

	@Produces @Named("xyz") // OK
	public String foo2;

	@Produces @Named // OK
	public String foo3() {return "";}

	@Produces @Named // Duplicate EL name
	public String foo4() {return "";}

	@Produces @Named("foo4") // Duplicate EL name
	public String foo5() {return "";}

	@Produces @TestS @Named("foo4")  // Duplicate EL name
	public String foo6() {return "";}

	@Produces @TestS // Duplicate EL name
	public String foo4;

	@Produces // OK
	public String foo7() {return "";}
}
