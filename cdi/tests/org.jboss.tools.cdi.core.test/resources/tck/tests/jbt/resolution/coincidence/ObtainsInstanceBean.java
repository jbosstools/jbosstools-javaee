package org.jboss.jsr299.tck.tests.jbt.resolution.coincidence;

import javax.inject.Inject;

import org.jboss.jsr299.tck.tests.jbt.resolution.AsynchronousPaymentProcessor;

class ObtainsInstanceBean {
   @Inject @PayBy(FirstPaymentMethod.CASH) AsynchronousPaymentProcessor cashPaymentProcessor;
}