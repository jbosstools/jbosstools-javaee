package org.jboss.generic4;

public interface MessageQueue {

	public MessageDispatcher createMessageDispatcher();

	public DispatcherPolicy getDispatcherPolicy();
}
