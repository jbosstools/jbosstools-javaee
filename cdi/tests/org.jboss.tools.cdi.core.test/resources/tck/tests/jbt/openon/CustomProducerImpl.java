package org.jboss.jsr299.tck.tests.jbt.openon;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;


public class CustomProducerImpl
{
   
   private static boolean disposedCorrectly = false;
   
   /**
    * @param log the log to set
    */
   public static void reset()
   {
      disposedCorrectly = false;
   }
   
   @Produces
   public Something produce(Something order)
   {
      return new Something();
   }
   
   public void dispose(@Disposes Something toDispose)
   {
      
   }
   
   /**
    * @return the disposedCorrectly
    */
   public static boolean isDisposedCorrectly()
   {
      return disposedCorrectly;
   }
   
   public class Something{
	   
   }

}
