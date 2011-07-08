package org.jboss.jsr299.tck.tests.jbt.openon;

import java.net.URL;

import javax.inject.Inject;

import org.jboss.seam.solder.resourceLoader.Resource;

public class ResourceLoader
{
   @Inject
   @Resource("WEB-INF/beans.xml")
   URL beansXml;

   @Inject
   @Resource("org/jboss/jsr299/tck/tests/jbt/openon/test.properties")
   URL properties1;

   @Inject
   @Resource("org/jboss/jsr299/tck/tests/jbt/openon/test")
   URL properties2;

   @Inject
   @Resource("org.jboss.jsr299.tck.tests.jbt.openon.test.properties")
   URL properties3;

   @Inject
   @Resource("org.jboss.jsr299.tck.tests.jbt.openon.test")
   URL properties4;

   private String foo;
   
   public ResourceLoader(String foo)
   {
      this.foo = foo;
   }
   
   /**
    * @return the foo
    */
   public String getFoo()
   {
      return foo;
   }

}
