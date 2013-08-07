package org.jboss.jsr299.tck.tests.jbt.validation.el;

public class TestBean {

	public String toString() {
		return "#{namedBean.foo}";
	}
}