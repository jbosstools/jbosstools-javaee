package org.jboss.jsr299.tck.tests.jbt.openon;

import javax.inject.Inject;

class LoggerConsumer
{
   @Inject
   // Event
   private Logger logger;

   public Logger getLogger()
   {
      return logger;
   }

   public void doSomething()
   {
      logger.log("Test message");
   }
}
