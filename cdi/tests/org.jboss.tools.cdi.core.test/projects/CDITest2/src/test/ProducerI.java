package test;

import javax.enterprise.inject.Produces;

import test.p1.I;

public class ProducerI {
	
	@Produces
	I getI() {
		return null;
	}

}
