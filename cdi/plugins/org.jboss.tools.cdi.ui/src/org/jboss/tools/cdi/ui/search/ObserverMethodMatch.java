/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.search;

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class ObserverMethodMatch extends Match {
	private IObserverMethod observerMethod;

	public ObserverMethodMatch(IObserverMethod observerMethod) {
		super(observerMethod, 0, 0);
		try{
			ISourceRange range = observerMethod.getMethod().getNameRange();
			setOffset(range.getOffset());
			setLength(range.getLength());
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}

		this.observerMethod = observerMethod;
	}

	public IObserverMethod getObserverMethod(){
		return observerMethod;
	}
}
