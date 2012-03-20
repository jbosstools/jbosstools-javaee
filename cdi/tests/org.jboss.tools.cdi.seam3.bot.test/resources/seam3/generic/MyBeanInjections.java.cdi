package cdi.seam;

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
	 * Injected method producer MyGenericBean.createMySecondBean()
	 * Configuration produced by MyConfigurationProducer.getOneConfig()
	 */
	@Inject
	MyBean2 second1;
	
	/**
	 * Injected method producer MyGenericBean.createMySecondBean()
	 * Configuration produced by MyConfigurationProducer.getSecondConfig()
	 */
	@Inject
	@Qualifier1
	MyBean2 second2;
	
	/**
	 * Injected method producer MyGenericBean.createMySecondBean()
	 * Configuration produced by MyExtendedConfiguration
	 */
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
	
}
