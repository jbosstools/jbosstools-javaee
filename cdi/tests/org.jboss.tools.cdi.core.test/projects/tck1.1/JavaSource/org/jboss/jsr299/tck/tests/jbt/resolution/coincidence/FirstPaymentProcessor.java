package org.jboss.jsr299.tck.tests.jbt.resolution;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.jsr299.tck.tests.jbt.resolution.AsynchronousPaymentProcessor;

@PayBy(FirstPaymentMethod.CASH)
@ApplicationScoped
class FirstPaymentProcessor implements AsynchronousPaymentProcessor {
   private int value = 0;

   public int getValue() {
      return value;
   }

   public void setValue(int value) {
      this.value = value;
   }
}