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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.impl.handlers.MoveHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XOrderedObject;
import org.jboss.tools.jsf.model.ReferenceObjectImpl;
import org.jboss.tools.jst.web.model.ReferenceObject;

public class OutputMoveHandler extends MoveHandler {

	public void executeHandler(XModelObject object, Properties prop) throws XModelException {
		if(!isEnabled(object)) return;int ii = 0;
		ReferenceObject o1 = (ReferenceObject)object;
		ReferenceObject o2 = (ReferenceObject)object.getModel().getModelBuffer().source();
		XModelObject r1 = o1.getReference();
		XModelObject r2 = o2.getReference();

		XModelObject p = r1.getParent();
		XOrderedObject oo = (XOrderedObject)p;
		int to = oo.getIndexOfChild(r1);
		int from = oo.getIndexOfChild(r2);
		boolean r = oo.move(from, to, true);
		if(!r) return;
	}

}
