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
 * where attribute 'id' is assigned to a CDI runtime extension implementation class qualified name 
 * and attribute 'class' is assigned to a JBoss Tools CDI model extension implementation class
 * qualified name.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface ICDIExtension {

}
