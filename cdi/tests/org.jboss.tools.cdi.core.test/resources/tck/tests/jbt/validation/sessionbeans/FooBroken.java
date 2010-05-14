package org.jboss.jsr299.tck.tests.jbt.validation.sessionbeans;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;

@RequestScoped
@Stateful
class FooBroken<T> implements LocalFoo {

	public void foo() {
	}
}