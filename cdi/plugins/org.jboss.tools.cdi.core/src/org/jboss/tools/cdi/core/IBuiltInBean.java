/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core;

/**
 * 3.6. Additional built-in beans.
 * A Java EE or embeddable EJB container must provide the following built-in beans, all of which 
 * have qualifier @Default, scope @ Dependent, and have no bean EL name:
 * UserTransaction, Principal, VlidationFactory, Validator, BeanManager.
 * 
 * 6.7.5. The Conversation interface
 * The container provides a built-in bean with bean type Conversation, scope @RequestScoped, and qualifier @Default,
 * named javax.enterprise.context.conversation.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBuiltInBean {

}
