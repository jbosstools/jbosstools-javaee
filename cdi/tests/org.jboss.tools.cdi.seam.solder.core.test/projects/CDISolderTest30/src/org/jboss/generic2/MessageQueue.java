package org.jboss.generic2;

public interface MessageQueue {

	public MessageDispatcher createMessageDispatcher();

	public DispatcherPolicy getDispatcherPolicy();
}
