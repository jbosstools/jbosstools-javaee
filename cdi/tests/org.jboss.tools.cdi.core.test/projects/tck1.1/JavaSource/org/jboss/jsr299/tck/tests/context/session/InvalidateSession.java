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
package org.jboss.jsr299.tck.tests.context.session;

import java.io.IOException;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InvalidateSession extends HttpServlet
{
   private static final long serialVersionUID = 1L;

   @Inject
   private BeanManager jsr299Manager;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      resp.setContentType("text/text");

      if (req.getParameter("timeout") != null)
      {
         SimpleSessionBean.setBeanDestroyed(false);
         org.jboss.jsr299.tck.impl.OldSPIBridge.getInstanceByType(jsr299Manager,SimpleSessionBean.class);
         req.getSession().setMaxInactiveInterval(Integer.parseInt(req.getParameter("timeout")));
      }
      else if (req.getParameter("isBeanDestroyed") != null)
      {
         resp.getWriter().print(SimpleSessionBean.isBeanDestroyed());
      }
      else
      {
         SimpleSessionBean.setBeanDestroyed(false);
         org.jboss.jsr299.tck.impl.OldSPIBridge.getInstanceByType(jsr299Manager,SimpleSessionBean.class);
         req.getSession().invalidate();
      }
   }
}
