package org.jboss.jsr299.tck.tests.jbt.openon;


/**
 * @author pmuir
 *
 */
public interface Producer
{
   
   public Foo produce();
   
   public void dispose(Foo foo);

}
