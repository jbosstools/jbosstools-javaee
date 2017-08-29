package org.jboss.jsr299.tck.tests.jbt.resolution.coincidence;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.jsr299.tck.tests.jbt.resolution.AsynchronousPaymentProcessor;

@PayBy(SecondPaymentMethod.CASH)
@ApplicationScoped
class SecondPaymentProcessor implements AsynchronousPaymentProcessor {
   private int value = 0;

   public int getValue() {
      return value;
   }

   public void setValue(int value) {
      this.value = value;
   }
}