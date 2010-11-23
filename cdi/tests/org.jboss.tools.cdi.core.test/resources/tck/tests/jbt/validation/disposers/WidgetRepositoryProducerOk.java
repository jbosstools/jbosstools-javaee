package org.jboss.jsr299.tck.tests.jbt.validation.disposers;

import javax.ejb.Stateless;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class WidgetRepositoryProducerOk {
   // NOTE cannot use producer field because Weld attempts to close EntityManager
   @PersistenceContext EntityManager em;

   public @Produces EntityManager retrieveEntityManager() {
      return em;
   }

   public void disposeEntityManager(@Disposes EntityManager em) {}
}