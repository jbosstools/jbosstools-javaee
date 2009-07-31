/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model;

import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class JSFCommentObjectImpl extends RegularObjectImpl {
	private static final long serialVersionUID = 4332569976226101607L;

	public String getPresentationString() {
		return JSFUIMessages.JSFCommentObjectImpl_Comment;
	}

}
