package org.jboss.jsr299.tck.tests.jbt.validation.inject.revalidation;

import javax.inject.Inject;

public class TestBeanForProducerBroken {

	@Inject Fruit fruit;
}