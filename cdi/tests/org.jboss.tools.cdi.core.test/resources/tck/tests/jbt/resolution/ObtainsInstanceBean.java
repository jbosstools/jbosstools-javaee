package org.jboss.jsr299.tck.tests.jbt.resolution;

import static org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CASH;
import static org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CHEQUE;
import static org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CREDIT_CARD;
import static org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.OTHER;

import javax.inject.Inject;

import org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod;

class ObtainsInstanceBean {
   @Inject @PayBy(CHEQUE) AsynchronousPaymentProcessor chequePaymentProcessor;

   @Inject @PayBy(PaymentMethod.CHEQUE) AsynchronousPaymentProcessor chequePaymentProcessor2;

   @Inject @PayBy(org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CHEQUE) AsynchronousPaymentProcessor chequePaymentProcessor3;

   @Inject @PayBy(CASH) AsynchronousPaymentProcessor cashPaymentProcessor;

   @Inject @PayBy(PaymentMethod.CASH) AsynchronousPaymentProcessor cashPaymentProcessor2;

   @Inject @PayBy(org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CASH) AsynchronousPaymentProcessor cashPaymentProcessor3;

   @Inject @PayBy(OTHER) AsynchronousPaymentProcessor otherPaymentProcessor;

   @Inject @PayBy(PaymentMethod.OTHER) AsynchronousPaymentProcessor otherPaymentProcessor2;

   @Inject @PayBy(org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.OTHER) AsynchronousPaymentProcessor otherPaymentProcessor3;

   @Inject @PayBy(PaymentMethod.CREDIT_CARD) AsynchronousPaymentProcessor unresolvedCreditCardPaymentProcessor;

   @Inject @PayBy(CREDIT_CARD) AsynchronousPaymentProcessor unresolvedCreditCardPaymentProcessor2;

   @Inject @PayBy(org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CREDIT_CARD) AsynchronousPaymentProcessor unresolvedCreditCardPaymentProcessor3;
}