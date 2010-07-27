package org.jboss.jsr299.tck.tests.jbt.openon;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;


/**
 * @author pmuir
 *
 */
public class CustomProducerImpl implements Producer
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
   public Foo produce()
   {
      return new Foo("foo!");
   }
   
   @Produces
   public Foo produce2(Foo order)
   {
      return new Foo("foo!");
   }
   
   public void dispose(@Disposes Foo foo)
   {
      disposedCorrectly = foo.getFoo().equals("decorated");
   }
   
   /**
    * @return the disposedCorrectly
    */
   public static boolean isDisposedCorrectly()
   {
      return disposedCorrectly;
   }

}
