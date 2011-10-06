package org.jboss.generic2;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;

public class MyMessageQueues {
	@Produces
	@ACMEQueue("defaultQueue")
	MessageSystemConfiguration defaultQueue = new MessageSystemConfiguration(null);

	@Produces
	@Durable
	@ConversationScoped
	@ACMEQueue("durableQueue")
	MessageSystemConfiguration producerDefaultQueue() {
		MessageSystemConfiguration config = new MessageSystemConfiguration(null);
//		config.setDurable(true);
		return config;
	}
}
