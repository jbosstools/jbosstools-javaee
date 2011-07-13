package cdi.test.alternative.case7;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is not selected.
 * Producer bean P is declared in B.p().
 * 
 * ASSERT: No eligible bean.
 */
public class X {
	@Inject P p;
}
