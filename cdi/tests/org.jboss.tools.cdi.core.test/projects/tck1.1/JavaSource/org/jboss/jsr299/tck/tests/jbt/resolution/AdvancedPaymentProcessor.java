package org.jboss.jsr299.tck.tests.jbt.resolution;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod;

@PayBy(PaymentMethod.CHEQUE)
@ApplicationScoped
class AdvancedPaymentProcessor implements AsynchronousPaymentProcessor {
   private int value = 0;

   public int getValue() {
      return value;
   }

   public void setValue(int value) {
      this.value = value;
   }
}