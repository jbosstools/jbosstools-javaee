package org.jboss.jsr299.tck.tests.jbt.validation.unproxyable;

import javax.inject.Inject;

public class InjectionPointBean_Broken { 
   @Inject TestType[] arrayBroken;
   @Inject TestType arrayOk;
   @Inject @TestQualifier TestType[] arrayOk2;
}