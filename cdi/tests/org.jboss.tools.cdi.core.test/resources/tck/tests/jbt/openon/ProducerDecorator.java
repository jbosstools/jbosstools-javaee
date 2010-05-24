package org.jboss.jsr299.tck.tests.jbt.openon;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;


/**
 * @author pmuir
 *
 */
@Decorator
public class ProducerDecorator implements Producer
{
   
   @Inject @Delegate
   private Producer producer;
   
   public Foo produce()
   {
      return new Foo(producer.produce().getFoo() + "!!");
   }
   
   /**
    * @param message the message to set
    */
   public static void reset()
   {
      
   }
   
   public void dispose(Foo foo)
   {
      producer.dispose(new Foo("decorated"));
   }


}
