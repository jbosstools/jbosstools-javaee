package org.jboss.jsr299.tck.tests.jbt.validation.unproxyable;

import javax.inject.Inject;

class Number_Broken {
   
   @SuppressWarnings("unused")
   @TestQualifier
   @Inject private int numberBroken;

   @SuppressWarnings("unused")
   @TestQualifier
   @Inject private long numberBroken2;

   @SuppressWarnings("unused")
   @TestQualifier
   @Inject private Short numberOk;

   @SuppressWarnings("unused")
   @TestQualifier
   @Inject private boolean numberOk1;

   @SuppressWarnings("unused")
   @Inject private BeanWithDefaultConsturctor numberOk2;
}