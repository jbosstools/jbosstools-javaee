package org.jboss.jsr299.tck.tests.jbt.validation.el;

public class TestBean {

	public String toString() {
		return "#{namedBean2.map.kk}";
	}

	public void run() {
		String s1 = "#{namedBean2.map.kk.bytes}";
		String s2 = "#{namedBean2.map.kk.abcd}";
	}
}