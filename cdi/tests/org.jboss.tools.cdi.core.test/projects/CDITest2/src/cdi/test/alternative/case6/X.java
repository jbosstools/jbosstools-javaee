package cdi.test.alternative.case6;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest2.
 * Alternative bean C is defined in CDITest2 project. It is selected in CDITest2.
 * 
 * ASSERT: Multiple beans: injection resolved to beans B and C.
 */
public class X {
	@Inject A a;
}
