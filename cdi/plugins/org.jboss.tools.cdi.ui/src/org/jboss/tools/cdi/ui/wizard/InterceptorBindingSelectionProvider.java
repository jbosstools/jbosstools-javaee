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
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class InterceptorBindingSelectionProvider extends CDIAnnotationSelectionProvider {

	public InterceptorBindingSelectionProvider() {}
	
	public ICDIAnnotation[] getSelectableObjects() {
		if(project == null) return new ICDIAnnotation[0];
		List<ICDIAnnotation> as = new ArrayList<ICDIAnnotation>();
		IInterceptorBinding[] ss = project.getInterceptorBindings();
		List c = (List)editor.getValue();
		for (IInterceptorBinding s: ss) {
			if(c.contains(s)) continue;
			IType t = s.getSourceType();
			if(t != null) {
				int kind = 0;
				try {
					kind = t.getFlags();
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
				if(!Flags.isPublic(kind)) {
					IPackageFragment p = t.getPackageFragment();
					if(packageFragment != null && !packageFragment.equals(p)) {
						continue;
					}
				}
			}
			as.add(s);
		}
		return as.toArray(new ICDIAnnotation[0]);
	}

	@Override
	protected String getDialogTitle() {
		return CDIUIMessages.SELECT_INTERCEPTOR_BINDING;
	}

}
