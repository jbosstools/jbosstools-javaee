package org.jboss.generic2;

import org.jboss.seam.solder.messages.Message;

public interface MessageDispatcher {

	void send(Message message);

}
