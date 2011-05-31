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
package org.jboss.tools.cdi.core.extension;

/**
 * This object represents CDI runtime extension in JBoss Tools CDI model.
 * It is not necessarily one to one mapping between CDI runtime extension implementation classes 
 * and JBoss Tools CDI model extension implementation classes.
 * Mapping is set by Eclipse extension point 'org.jboss.tools.cdi.core.cdiextensions'
 * where attribute 'runtime' is assigned to a CDI runtime extension implementation class qualified name 
 * and attribute 'class' is assigned to a JBoss Tools CDI model extension implementation class
 * qualified name.
 * It is not planned to add methods to this interface, all features that are to be provided 
 * by implementations, will be available either through IAdaptable or by direct implementing feature
 * interfaces. In that way, adding to core support of a new feature will not require to implement 
 * new methods in existing extensions. 
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface ICDIExtension {

}