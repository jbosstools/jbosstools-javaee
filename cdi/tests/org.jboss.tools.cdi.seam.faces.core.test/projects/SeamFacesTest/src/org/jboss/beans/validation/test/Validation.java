package org.jboss.beans.validation.test;

import javax.inject.Inject;

import org.jboss.seam.faces.validation.InputField;

public class Validation {

	@Inject @InputField MyBean1 beanOk;

	@Inject @InputField String bean2Ok;

	@Inject @InputField
	public void setMyBeanOk(MyBean1 bean, String bean2) {
		
	}

	@Inject MyBean1 beanBroken;

	@Inject String bean2Broken;

	@Inject
	public void setMyBeanBroken(MyBean1 bean) {
		
	}

	@Inject
	public void setMyBeanBroken(String bean) {
		
	}
}