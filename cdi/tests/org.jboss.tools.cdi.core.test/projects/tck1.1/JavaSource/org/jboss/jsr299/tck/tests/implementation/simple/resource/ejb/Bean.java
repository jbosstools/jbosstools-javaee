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
package org.jboss.jsr299.tck.tests.implementation.simple.resource.ejb;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class Bean implements BeanRemote
{
   private int knocks = 0;
   
   private @Inject Monitor monitor;
   
   private @Resource UserTransaction transaction;
   
   public String knockKnock()
   {
      knocks++;
      return "We're home";
   }
   
   public int getKnocks()
   {
      return knocks;
   }
   
   public boolean isUserTransactionInjected()
   {
      try
      {
         if (transaction != null)
         {
            transaction.getStatus();
            return true;
         }
      }
      catch (SystemException e)
      {
      }
      return false;
   }

   @PreDestroy 
   public void cleanup()
   {
      monitor.remoteEjbDestroyed();
   }

   @Remove
   public void dispose()
   {
   }
}
