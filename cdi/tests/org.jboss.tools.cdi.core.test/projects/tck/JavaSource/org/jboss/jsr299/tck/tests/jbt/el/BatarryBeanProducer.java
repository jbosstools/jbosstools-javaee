package org.jboss.jsr299.tck.tests.jbt.el;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class BatarryBeanProducer {

	@Produces
	@Named("alkalineBatarry")
	public Batarry batarry = new Batarry();
}