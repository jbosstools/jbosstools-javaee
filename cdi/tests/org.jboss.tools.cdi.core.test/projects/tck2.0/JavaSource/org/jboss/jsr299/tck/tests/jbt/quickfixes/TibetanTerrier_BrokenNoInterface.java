package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

@Stateless
class TibetanTerrier_BrokenNoInterface
{
   void observeSomeEvent(@Observes String someEvent)
   {
   }
}
