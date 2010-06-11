/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class StereotypesSelectionProvider extends CDIAnnotationSelectionProvider {

	public StereotypesSelectionProvider() {}
	
	public ICDIAnnotation[] getSelectableObjects() {
		if(project == null) return new ICDIAnnotation[0];
		List<ICDIAnnotation> as = new ArrayList<ICDIAnnotation>();
		IStereotype[] ss = project.getStereotypes();
		List c = (List)editor.getValue();
		for (IStereotype s: ss) {
			if(c.contains(s)) continue;
			as.add(s);
		}
		return as.toArray(new ICDIAnnotation[0]);
	}

	@Override
	protected String getDialogTitle() {
		return CDIUIMessages.SELECT_STEREOTYPE;
	}

}
