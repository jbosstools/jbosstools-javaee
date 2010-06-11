package org.jboss.jsr299.tck.tests.jbt.validation.inject;

import javax.inject.Inject;

@SuppressWarnings("unused")
class GameBroken {
   @Inject @PrimitiveTestQualifer private int numberBroken;

   @Inject @PrimitiveTestQualifer private Integer number2;
   @Inject @TestQualifer private int number3;
   @Inject @TestQualifer private Integer number4;

   @Inject
   public void setNumber() {
	   
   }

   @Inject
   public void setNumber2(@PrimitiveTestQualifer int numberBroken,
		   @PrimitiveTestQualifer Integer number2,
		   @TestQualifer int number3,
		   @TestQualifer Integer number4) {
   }
}