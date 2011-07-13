package cdi.test.alternative.case5;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
 * Alternative bean C is defined in CDITest2 project. It is selected in CDITest2.
 * 
 * ASSERT: Injection resolved to bean C.
 */
public class X {
	@Inject A a;
}
