package org.jboss.generic2;

import org.jboss.solder.messages.Message;

public interface MessageDispatcher {

	void send(Message message);

}
