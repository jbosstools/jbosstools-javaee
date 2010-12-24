package cdi.test5;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import test.TestScope;

public class TestBean5 {

	@Inject boolean test;

	@TestScope
	@Produces boolean foo() {
		return true;
	}
}