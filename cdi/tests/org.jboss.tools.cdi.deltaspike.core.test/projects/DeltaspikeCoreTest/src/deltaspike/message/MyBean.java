package deltaspike.message;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.message.MessageContext;

public class MyBean {

	@Inject
	SimpleMessage messages;

	@Inject
	MessageContext messageContext;
}
