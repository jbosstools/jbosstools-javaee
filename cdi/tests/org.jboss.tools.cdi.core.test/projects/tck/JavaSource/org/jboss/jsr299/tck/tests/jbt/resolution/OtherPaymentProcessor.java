package org.jboss.jsr299.tck.tests.jbt.resolution;

import javax.enterprise.context.ApplicationScoped;

@PayBy(org.jboss.jsr299.tck.tests.jbt.resolution.PayBy.PaymentMethod.OTHER)
@ApplicationScoped
class OtherPaymentProcessor implements AsynchronousPaymentProcessor {
   private int value = 0;

   public int getValue() {
      return value;
   }

   public void setValue(int value) {
      this.value = value;
   }
}