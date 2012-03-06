/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.views;

import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;

/**
 * This implementation is designed for standard Projects Explorer 
 * view to provide 'Seam Components' name for the root node of
 * the appended sub-tree.
 *  
 * @author Viacheslav Kabanovich
 */
public class SeamProjectLabelProvider extends SeamLabelProvider {
	
	public SeamProjectLabelProvider() {}

	@Override
	public String getText(Object element) {
		if(element instanceof ISeamProject) {
			return SeamCoreMessages.SEAM_PROJECT_LABEL_PROVIDER_SEAM_COMPONENTS;
		}		
		return super.getText(element);
	}

}
