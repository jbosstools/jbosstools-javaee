package org.jboss.jsr299.tck.tests.jbt.validation.inject;

import javax.inject.Inject;

class FarmBroken <U> {
   @Inject
   public <T extends Animal> void setAnimal(T animal) {
      
   }

   @Inject
   public void setAnimal(U animal) {
   }

   @Inject
   public U animalU;
}