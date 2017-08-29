package org.jboss.jsr299.tck.tests.jbt.resolution.expressions;

import javax.inject.Inject;

public class SomeBean {
	
	@Inject @IntQualifier(width=(int)(BeanFactory.W + 2) - 2, height=(int)'b' - (int)'a' + 9)
	String s;

}
