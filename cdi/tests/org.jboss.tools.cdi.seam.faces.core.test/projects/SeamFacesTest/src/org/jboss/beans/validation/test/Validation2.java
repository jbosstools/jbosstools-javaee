package org.jboss.beans.validation.test;

import javax.inject.Inject;

import org.jboss.seam.faces.validation.InputElement;

public class Validation2 {

	@Inject InputElement<MyBean1> bean3Ok;

	@Inject InputElement<String> bean4Ok;

	@Inject
	public void setMyBeanOk(InputElement<MyBean1> bean, InputElement<String> bean2) {
		
	}
}