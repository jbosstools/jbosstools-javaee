package org.jboss.jsr299.tck.tests.jbt.validation.unproxyable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

class NumberProducer {

   @Produces
   @RequestScoped
   @TestQualifier
   public int produce() {
      return 0;
   }

   @Produces
   @RequestScoped
   @TestQualifier
   public long foo;

   @Produces
   @RequestScoped
   @TestQualifier
   public short foo2;

   @Produces
   @TestScope
   @TestQualifier
   public boolean foo3;
}