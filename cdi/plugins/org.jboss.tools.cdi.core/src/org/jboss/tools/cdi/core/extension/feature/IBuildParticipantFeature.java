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

import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;

/**
 * This feature includes BeforeBeanDiscovery event of CDI runtime, but also it should provide
 * facilities for incremental build and clean of project at design time. 
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBuildParticipantFeature {
	
	public void setProject(CDICoreNature n);

	public void buildIsAboutToBegin();

	public IDefinitionContextExtension getContext();

}
