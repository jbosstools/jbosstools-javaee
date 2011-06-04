package org.jboss.generic2;

import javax.inject.Inject;

public class DurableMessageLogger {

	@Inject
	@Durable
	MessageDispatcher dispatcher;

	@Inject
	@Durable
	DispatcherPolicy policy;

	/* Tweak the dispatch policy to enable duplicate removal */
	@Inject
	void tweakPolicy(@Durable DispatcherPolicy policy) {
		policy.removeDuplicates();
	}

	void logMessage(Payload payload) {
	}
}
