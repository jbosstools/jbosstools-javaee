package org.jboss.jsr299.tck.tests.jbt.validation.unproxyable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

public class ArrayProducer {

   @Produces @RequestScoped
   public TestType[] produce() {
      return new TestType[0];
   }

   @Produces @TestQualifier @TestScope
   public TestType[] produce2() {
      return new TestType[0];
   }
}