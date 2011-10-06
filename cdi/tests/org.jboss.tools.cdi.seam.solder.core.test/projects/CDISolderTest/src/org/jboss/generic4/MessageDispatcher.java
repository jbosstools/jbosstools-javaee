package org.jboss.generic4;

import org.jboss.solder.messages.Message;

public interface MessageDispatcher {

	void send(Message message);

}
