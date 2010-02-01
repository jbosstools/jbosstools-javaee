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
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class InjectionPointMatchPresentation implements IMatchPresentation {
	private static InjectionPointLabelProvider labelProvider = new InjectionPointLabelProvider();

	public ILabelProvider createLabelProvider() {
		return labelProvider;
	}

	public void showMatch(Match match, int currentOffset, int currentLength,
			boolean activate) throws PartInitException {
		if(match instanceof InjectionPointMatch){
			try{
				JavaUI.openInEditor(((InjectionPointMatch)match).getBean().getBeanClass());
			}catch(JavaModelException ex){
				CDIUIPlugin.getDefault().logError(ex);
			}catch(PartInitException ex){
				CDIUIPlugin.getDefault().logError(ex);
			}
		}
	}

}
