/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.util.Set;
import org.eclipse.core.resources.IProjectNature;

public interface ISeamProject extends IProjectNature {
	public static String NATURE_ID = "org.jboss.tools.seam.core.seam";
	
	public ISeamComponent getComponent(String name);
	public Set<ISeamComponent> getComponents();

}
