package org.jboss.tools.seam.jms.validation.test;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.seam.jms.annotations.JmsDestination;

public class JmsResourceInjection {

	@Inject Queue broken;

	@Inject @JmsDestination(jndiName = "/jms/Q") Connection cOk;
	@Inject @MyQualifier Connection c1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") Session sOk;
	@Inject @MyQualifier Session s1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") Topic tOk;
	@Inject @MyQualifier Topic t1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") Queue qOk;
	@Inject @MyQualifier Queue q1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") TopicPublisher tpOk;
	@Inject @MyQualifier TopicPublisher tp1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") QueueSender qsOk;
	@Inject @MyQualifier QueueSender qs1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") TopicSubscriber tsOk;
	@Inject @MyQualifier TopicSubscriber ts1Ok;

	@Inject @JmsDestination(jndiName = "/jms/Q") QueueReceiver qrOk;
	@Inject @MyQualifier QueueReceiver qr1Ok;
}