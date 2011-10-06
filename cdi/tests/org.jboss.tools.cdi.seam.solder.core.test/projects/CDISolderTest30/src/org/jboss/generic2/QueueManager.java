package org.jboss.generic2;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.solder.bean.generic.ApplyScope;
import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;
import java.io.Serializable;

@GenericConfiguration(ACMEQueue.class)
@ApplyScope
public class QueueManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@Generic
	MessageSystemConfiguration systemConfig;

	@Inject
	ACMEQueue config;

//	MessageQueueFactory factory;

	@PostConstruct
	void init() {
//		factory = systemConfig.createMessageQueueFactory();
	}

	@Produces
	@ApplyScope
	public MessageQueue messageQueueProducer() {
//		return factory.createMessageQueue(config.name());
		return null;
	}
}
