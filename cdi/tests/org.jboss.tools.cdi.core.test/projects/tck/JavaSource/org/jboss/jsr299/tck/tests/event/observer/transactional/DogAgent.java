/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.jsr299.tck.tests.event.observer.transactional;

import static javax.ejb.TransactionManagementType.BEAN;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.UserTransaction;

@Stateless
@TransactionManagement(BEAN)
@Named
@Default
public class DogAgent implements Agent
{
   @Resource
   private UserTransaction userTransaction;

   @Inject
   private BeanManager jsr299Manager;

   public void sendInTransaction(Object event)
   {
      try
      {
         userTransaction.begin();
         jsr299Manager.fireEvent(event);
         userTransaction.commit();
      }
      catch (EJBException ejbException)
      {
         throw ejbException;
      }
      catch (Exception e)
      {
         throw new EJBException("Transaction failure", e);
      }
   }
   
   public void sendInTransactionAndFail(Object event) throws Exception
   {
      try
      {
         userTransaction.begin();
         jsr299Manager.fireEvent(event);
         throw new FooException();
      }
      finally
      {
         userTransaction.rollback();
      }
      
   }

   public void sendOutsideTransaction(Object event)
   {
      jsr299Manager.fireEvent(event);
   }
}
