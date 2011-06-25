package org.jboss.generic4;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class MyMessageQueues {
	@Produces
	@ACMEQueue("defaultQueue")
	@Named("aaa")
	MessageSystemConfiguration defaultQueue = new MessageSystemConfiguration(null);

	@Produces
	@Durable
	@ConversationScoped
	@ACMEQueue("durableQueue")
	@Named("aaa")
	MessageSystemConfiguration producerDefaultQueue() {
		MessageSystemConfiguration config = new MessageSystemConfiguration(null);
//		config.setDurable(true);
		return config;
	}
}