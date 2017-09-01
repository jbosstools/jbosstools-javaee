package org.jboss.jsr299.tck.tests.jbt.validation.weld;

import java.util.List;

import javax.inject.Inject;

import org.jboss.weld.environment.se.bindings.Parameters;

public class WeldBean {

	@Inject @Parameters List<String> parametersList;
	@Inject @Parameters String[] parametersArray;
}