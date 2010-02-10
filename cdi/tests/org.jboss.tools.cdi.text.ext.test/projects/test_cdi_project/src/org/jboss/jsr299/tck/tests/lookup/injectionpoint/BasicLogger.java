package org.jboss.jsr299.tck.tests.lookup.injectionpoint;

import javax.decorator.Decorator;

@Decorator
class BasicLogger implements Logger
{
   
   private static String message;

   public String getMessage()
   {
      return message;
   }

   public void log(String message)
   {
      BasicLogger.message = message;
   }

}
