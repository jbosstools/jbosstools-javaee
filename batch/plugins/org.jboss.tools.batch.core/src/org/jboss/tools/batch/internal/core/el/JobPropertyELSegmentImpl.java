/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.el;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.el.core.parser.LexicalToken;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IOpenableReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobPropertyELSegmentImpl extends ELSegmentImpl {
//	private ITextSourceReference jobPropertySourceReference;
	
	List<? extends IOpenableReference> attrs = new ArrayList<IOpenableReference>();

	public JobPropertyELSegmentImpl(LexicalToken token) {
		super(token);
	}

	public void setAttrs(List<? extends IOpenableReference> attrs) {
		this.attrs = attrs;
	}

	public IOpenableReference[] getOpenable() {
		return attrs.toArray(new IOpenableReference[attrs.size()]);
	}

}
