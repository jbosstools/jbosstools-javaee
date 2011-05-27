package org.jboss.generic;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

public class MyBeanInjections {
	
	/**
	 * Injected method producer MyGenericBean.createMyFirstBean()
	 * Configuration produced by MyConfigurationProducer.getOneConfig()
	 */
	@Inject
	MyBean first1;

	/**
	 * Injected method producer MyGenericBean.createMyFirstBean()
	 * Configuration produced by MyConfigurationProducer.getSecondConfig()
	 */
	@Inject
	@Qualifier1
	MyBean first2;

	/**
	 * Injected method producer MyGenericBean.createMyFirstBean()
	 * Configuration produced by MyExtendedConfiguration
	 */
	@Inject
	@Qualifier2
	MyBean first3;

	/**
	 * Injected method producer MyGenericBean.createMyFirstBean()
	 * Configuration produced by MyConfigurationProducer.fourthConfig
	 */
	@Inject
	@Qualifier4("Fourth")
	MyBean first4;

	/**
	 * Injected method producer MyGenericBean.createMyFirstBean()
	 * Configuration produced by MyConfigurationProducer.fifthConfig
	 */
	@Inject
	@Qualifier4("Fifth")
	MyBean first5;

	@Inject
	MyBean2 second1;

	@Inject
	@Qualifier1
	MyBean2 second2;

	@Inject
	@Qualifier2
	MyBean2 second3;

	/**
	 * Injected field producer MyGenericBean.myThirdBean
	 * Configuration produced by MyConfigurationProducer.getOneConfig()
	 */
	@Inject
	MyBean3 third1;

	/**
	 * Injected field producer MyGenericBean.myThirdBean
	 * Configuration produced by MyConfigurationProducer.getSecondConfig()
	 */
	@Inject
	@Qualifier1
	MyBean3 third2;

	/**
	 * Injected field producer MyGenericBean.myThirdBean
	 * Configuration produced by MyExtendedConfiguration
	 */
	@Inject
	@Qualifier2
	MyBean3 third3;

	/**
	 * Injected field producer MyGenericBean.myThirdBean
	 * Configuration produced by MyConfigurationProducer.fourthConfig
	 */
	@Inject
	@Qualifier4("Fourth")
	MyBean3 third4;

	/**
	 * Injected field producer MyGenericBean.myThirdBean
	 * Configuration produced by MyConfigurationProducer.fifthConfig
	 */
	@Inject
	@Qualifier4("Fifth")
	MyBean3 third5;

	@Inject 
	void setAllMyBean4(@Default MyBean4 fourth1,
			@Qualifier1 MyBean4 fourth2,
			@Qualifier2 MyBean4 fourth3,
			@Qualifier4("Fourth") MyBean4 fourth4,
			@Qualifier4("Fifth") MyBean4 fourth5
			) {
		
	}

	@Inject
	@Any
	Event<MyBean> event1;

	@Inject
	@Qualifier1
	Event<MyBean> event2;

	@Inject
	@Qualifier2
	Event<MyBean> event3;

	
}
