package org.jboss.jsr299.tck.tests.jbt.lookup;

import javax.enterprise.inject.Alternative;

@Alternative
public class AlternativePaymentProcessor extends PaymentProcessorImpl {

	public AlternativePaymentProcessor() {
	}
}