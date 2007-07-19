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

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProjectLabelProvider extends SeamLabelProvider {
	
	public SeamProjectLabelProvider() {}

	public String getText(Object element) {
		if(element instanceof ISeamProject) {
			return "Seam Components";
		}		
		return super.getText(element);
	}

}
