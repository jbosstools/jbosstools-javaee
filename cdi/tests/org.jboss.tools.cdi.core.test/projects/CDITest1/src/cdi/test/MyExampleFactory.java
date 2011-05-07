package cdi.test;

import javax.enterprise.inject.Produces;

import org.jboss.cdi.test.example.Example;

public class MyExampleFactory {

	@Produces
	public Example createExample() {
		return new Example();
	}
}
