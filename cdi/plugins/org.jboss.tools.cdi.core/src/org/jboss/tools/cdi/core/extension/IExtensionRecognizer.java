/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.extension;

import org.eclipse.jdt.core.IJavaProject;

/**
 * Normally, CDI extension given by 'runtime' is registered in
 * META-INF/services/javax.enterprise.inject.spi.Extension
 * However, CDI or server implementations may prefer adding 
 * their system extensions directly.
 * 
 * To include such extensions into CDI model, 'runtime' should be 
 * added to extension point org.jboss.tools.cdi.core.cdiextensions
 * with attribute 'recognizer' set to implementation of this interface.
 * Default implementation just checks that class represented by 'runtime'
 * exists in the classpath of Java project.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IExtensionRecognizer {

	public boolean containsExtension(String runtime, IJavaProject javaProject); 
}
