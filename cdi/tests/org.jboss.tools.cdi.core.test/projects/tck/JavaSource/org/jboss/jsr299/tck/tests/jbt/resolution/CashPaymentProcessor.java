package org.jboss.jsr299.tck.tests.jbt.resolution;

import static org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.CASH;

import javax.enterprise.context.ApplicationScoped;

@PayBy(CASH)
@ApplicationScoped
class CashPaymentProcessor implements AsynchronousPaymentProcessor {
   private int value = 0;

   public int getValue() {
      return value;
   }

   public void setValue(int value) {
      this.value = value;
   }
}