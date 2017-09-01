package org.jboss.jsr299.tck.tests.jbt.openon;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class AdvancedFood{
	@Inject private Food food;
	   
	   @Produces Food ff;

	   public Food getFood()
	   {
	      return food;
	   }

	   public void doSomething()
	   {
	      food.eat("Test message");
	   }
	
}
