package cdi.test.alternative.case4;

import javax.inject.Inject;

/*
 * Bean A is defined in CDITest1 project.
 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
 * Alternative bean C is defined in CDITest2 project. It is not selected.
 * Bean Y is accessed through project CDITest2
 * ASSERT: Injection resolved to bean B.
 */
public class Y {
	@Inject B b;
}
