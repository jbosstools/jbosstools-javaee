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
package org.jboss.tools.cdi.core.extension.feature;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModelObject;

/**
 * This feature visits any jar in classpath.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBuildParticipant2Feature extends IBuildParticipantFeature {

	/**
	 * Looks for artifacts in any jar entries.
	 * 
	 * @param path
	 * @param beansXML
	 */
	public void visitJar(IPath path, XModelObject fs);

}
