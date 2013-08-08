package org.jboss.jsr299.tck.tests.jbt.openon;

import javax.inject.Inject;

class FoodConsumer
{
   @Inject
   // Event
   private Food food;

   public Food getFood()
   {
      return food;
   }

   public void doSomething()
   {
      food.eat("Test message");
   }
}
