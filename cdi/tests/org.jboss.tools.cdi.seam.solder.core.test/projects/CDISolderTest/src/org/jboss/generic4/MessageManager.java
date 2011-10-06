package org.jboss.generic4;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.solder.bean.generic.ApplyScope;
import org.jboss.solder.bean.generic.Generic;
import org.jboss.solder.bean.generic.GenericConfiguration;

@GenericConfiguration(ACMEQueue.class)
class MessageManager {

	@Inject
	@Generic
	MessageQueue queue;

	@Produces
	@ApplyScope
	MessageDispatcher messageDispatcherProducer() {
		return queue.createMessageDispatcher();
	}

	@Produces
	DispatcherPolicy getPolicy() {
		return queue.getDispatcherPolicy();
	}
}
