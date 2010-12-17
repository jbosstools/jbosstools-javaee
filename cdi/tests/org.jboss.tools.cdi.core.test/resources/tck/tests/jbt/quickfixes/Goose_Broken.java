package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.inject.Inject;

class Goose_Broken
{
   
   @Inject
   public Goose_Broken(String foo)
   {
   }
   
   @Inject
   public Goose_Broken(String foo, Double bar)
   {
      
   }

}
