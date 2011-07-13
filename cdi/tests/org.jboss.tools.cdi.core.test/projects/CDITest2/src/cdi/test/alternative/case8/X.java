package cdi.test.alternative.case8;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
 * Producer bean P is declared in B.p().
 * 
 * ASSERT: Injection resolved to bean B.p().
 */
public class X {
	@Inject P p;
}
