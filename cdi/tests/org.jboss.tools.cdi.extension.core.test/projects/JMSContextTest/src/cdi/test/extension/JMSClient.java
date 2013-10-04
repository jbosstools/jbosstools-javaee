package cdi.test.extension;

import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.JMSContext;

@ApplicationScoped
public class JMSClient {
	@Inject JMSContext f;
}
