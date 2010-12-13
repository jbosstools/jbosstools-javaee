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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class QualifierSelectionProvider extends CDIAnnotationSelectionProvider {

	public QualifierSelectionProvider() {}
	
	public ICDIAnnotation[] getSelectableObjects() {
		if(project == null) return new ICDIAnnotation[0];
		List<ICDIAnnotation> as = new ArrayList<ICDIAnnotation>();
		IQualifier[] ss = project.getQualifiers();
		List c = editor == null ? null : (List)editor.getValue();
		for (IQualifier s: ss) {
			if(c != null && c.contains(s)) continue;
			as.add(s);
		}
		return as.toArray(new ICDIAnnotation[0]);
	}

	@Override
	protected String getDialogTitle() {
		return CDIUIMessages.SELECT_QUALIFIER;
	}

}
