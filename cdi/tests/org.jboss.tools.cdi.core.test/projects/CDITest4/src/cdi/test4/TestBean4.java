package cdi.test4;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import test.TestScope;

public class TestBean4 {

	@Inject int test;

	@TestScope
	@Produces int foo() {
		return 0;
	}
}