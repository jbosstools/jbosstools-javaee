/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.jboss.tools.cdi.core.ICDIAnnotation;

public class CDIAnnotationLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if(element instanceof ICDIAnnotation) {
			return ((ICDIAnnotation)element).getSourceType().getFullyQualifiedName();
		}
		return super.getText(element);
	}

}
