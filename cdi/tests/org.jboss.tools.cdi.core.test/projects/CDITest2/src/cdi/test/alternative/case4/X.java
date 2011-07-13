package cdi.test.alternative.case4;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
 * Alternative bean C is defined in CDITest2 project. It is not selected.
 * 
 * ASSERT: Injection resolved to bean A.
 */
public class X {
	@Inject A a;
}
