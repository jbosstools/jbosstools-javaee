package cdi.test.alternative.case3;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest2.
 * Alternative bean C is defined in CDITest2 project. It is not selected.
 * 
 * ASSERT: Injection resolved to bean B.
 */
public class X {
	@Inject A a;
}
