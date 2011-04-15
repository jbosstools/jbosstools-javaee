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
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class EventMatch extends Match {
	private IInjectionPoint event;

	public EventMatch(IInjectionPoint event) {
		super(event, 0, 0);
		try{
			ISourceRange range = null;
			if(event instanceof IInjectionPointField){
				range = ((IInjectionPointField)event).getField().getNameRange();
			}else if(event instanceof IInjectionPointMethod){
				range = ((IInjectionPointMethod)event).getMethod().getNameRange();
			}else if(event instanceof IInjectionPointParameter){
				range = ((IInjectionPointParameter)event).getBeanMethod().getMethod().getNameRange();
			}

			if(range != null){
				setOffset(range.getOffset());
				setLength(range.getLength());
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		this.event = event;
	}

	public IInjectionPoint getEvent(){
		return event;
	}
}
