package org.jboss.generic;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

public class MyConfigurationProducer {
	
	/**
	 * Configuration created by field producer.
	 */
	@Produces
	@MyGenericType("first")
	@Default
	MyConfiguration getOneConfig() {
		return null;
	}

	/**
	 * Configuration created by method producer with specified scope.
	 */
	@Produces
	@MyGenericType("second")
	@Qualifier1
	@SessionScoped
	MyConfiguration getSecondConfig() {
		return null;
	}

	/**
	 * Configuration created by field producer; qualifier has value.
	 */
	@Produces
	@MyGenericType("fourth")
	@Qualifier4("Fourth")
	MyConfiguration fourthConfig = new MyConfiguration("fourth");

	/**
	 * Configuration created by field producer with specified scope; qualifier has value.
	 */
	@Produces
	@MyGenericType("fifth")
	@Qualifier4("Fifth")
	@SessionScoped
	MyConfiguration fifthConfig = new MyConfiguration("fifth");

}
