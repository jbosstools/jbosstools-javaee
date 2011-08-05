/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.persistence;

/**
 * 
 * @author Viacheslav Kabanlvich
 *
 */
public interface CDIPersistenceConstants {
	public String EXTENSION_MANAGED_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.ExtensionManaged";

	public String ENTITY_MANAGER_FACTORY_TYPE_NAME = "javax.persistence.EntityManagerFactory";
	public String ENTITY_MANAGER_TYPE_NAME = "javax.persistence.EntityManager";

	public String SESSION_TYPE_NAME = "org.hibernate.Session";
	public String SESSION_FACTORY_TYPE_NAME = "org.hibernate.SessionFactory";

}
