/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.search;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class InjectionPointMatchPresentation implements IMatchPresentation {
	private static InjectionPointLabelProvider labelProvider = new InjectionPointLabelProvider();

	public ILabelProvider createLabelProvider() {
		return labelProvider;
	}

	public void showMatch(Match match, int currentOffset, int currentLength,
			boolean activate) throws PartInitException {
		
		try{
			if(match instanceof BeanMatch){
				JavaUI.openInEditor(((BeanMatch)match).getBean().getBeanClass());
			}else if(match instanceof ObserverMethodMatch){
				JavaUI.openInEditor(((ObserverMethodMatch)match).getObserverMethod().getMethod());
			}else if(match instanceof EventMatch){
				IInjectionPoint iPoint = ((EventMatch)match).getEvent();
				if(iPoint instanceof IInjectionPointField){
					JavaUI.openInEditor(((IInjectionPointField)iPoint).getField());
				}else if(iPoint instanceof IInjectionPointMethod){
					JavaUI.openInEditor(((IInjectionPointMethod)iPoint).getMethod());
				}else if(iPoint instanceof IInjectionPointParameter){
					JavaUI.openInEditor(((IInjectionPointParameter)iPoint).getBeanMethod().getMethod());
				}
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}catch(PartInitException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

}
