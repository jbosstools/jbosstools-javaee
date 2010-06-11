package org.jboss.jsr299.tck.tests.jbt.validation.inject;

import javax.enterprise.inject.Produces;

class NumberProducer {

   @Produces
   @PrimitiveTestQualifer
   public Integer getNumber() {
      return null;
   }

   @Produces
   @TestQualifer
   public int getNumber2() {
      return 0;
   }
}