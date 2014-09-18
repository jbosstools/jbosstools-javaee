package deltaspike.message;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.message.MessageContext;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyBean {

	@Inject
	SimpleMessage messages;

	@Inject
	MessageContext messageContext;
}
