package cdi.test3;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import test.TestScope;

public class TestBean3 {

	@Inject boolean test;

	@TestScope
	@Produces boolean foo() {
		return true;
	}
}