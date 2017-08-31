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
package org.jboss.jsr299.tck.tests.extensions.afterBeanDiscovery;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.jsr299.tck.literals.DefaultLiteral;

public class AfterBeanDiscoveryObserver implements Extension
{
   
   private static boolean processBeanFiredForCockatooBean;
   
   public static boolean isProcessBeanFiredForCockatooBean()
   {
      return processBeanFiredForCockatooBean;
   }
   
   public void observeProcessBean(@Observes ProcessBean<Cockatoo> event)
   {
      AfterBeanDiscoveryObserver.processBeanFiredForCockatooBean = true;
      assert event.getBean().getName().equals("cockatoo");
   }
   
   public void addABean(@Observes AfterBeanDiscovery afterBeanDiscovery)
   {
      afterBeanDiscovery.addBean(new Bean<Cockatoo>()
      {
         
         private final Set<Annotation> qualifiers = new HashSet<Annotation>(Arrays.asList(new DefaultLiteral()));
         private final Set<Type> types = new HashSet<Type>(Arrays.<Type>asList(Cockatoo.class));

         public Class<?> getBeanClass()
         {
            return Cockatoo.class;
         }

         public Set<InjectionPoint> getInjectionPoints()
         {
            return Collections.emptySet();
         }

         public String getName()
         {
            return "cockatoo";
         }

         public Set<Annotation> getQualifiers()
         {
            return qualifiers;
         }

         public Class<? extends Annotation> getScope()
         {
            return Dependent.class;
         }

         public Set<Class<? extends Annotation>> getStereotypes()
         {
            return Collections.emptySet();
         }

         public Set<Type> getTypes()
         {
            return types;
         }

         public boolean isAlternative()
         {
            return false;
         }

         public boolean isNullable()
         {
            return true;
         }

         public Cockatoo create(CreationalContext<Cockatoo> creationalContext)
         {
            return new Cockatoo("Billy");
         }

         public void destroy(Cockatoo instance, CreationalContext<Cockatoo> creationalContext)
         {
            // No-op
         }
      });
   }

}
