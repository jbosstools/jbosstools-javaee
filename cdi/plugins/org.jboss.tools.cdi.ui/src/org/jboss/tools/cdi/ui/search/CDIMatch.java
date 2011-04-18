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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.ICDIElement;

public class CDIMatch extends Match {
	public CDIMatch(ICDIElement element) {
		super(new CDIElementWrapper(element), 0, 0);
	}
	
	public String getLabel(){
		return ((CDIElementWrapper)getElement()).getLabel();
	}

	public String getPath(){
		return ((CDIElementWrapper)getElement()).getPath();
	}

	public IJavaElement getJavaElement(){
		return ((CDIElementWrapper)getElement()).getJavaElement();
	}

	public ICDIElement getCDIElement(){
		return ((CDIElementWrapper)getElement()).getCDIElement();
	}
	
}
