package cdi.test3;

import javax.inject.Inject;

import cdi.test.MyBean;
import cdi.test.MyQualifier;

public class C {
	@Inject @MyQualifier MyBean bean;
}
