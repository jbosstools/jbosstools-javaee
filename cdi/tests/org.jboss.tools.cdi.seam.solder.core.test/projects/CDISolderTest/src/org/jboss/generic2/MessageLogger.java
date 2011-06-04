package org.jboss.generic2;

import javax.inject.Inject;

public class MessageLogger {

	@Inject
	MessageDispatcher dispatcher;

	void logMessage(Payload payload) {
	      /* Add metaddata to the message */
	      dispatcher.send(null);
	   }
}
