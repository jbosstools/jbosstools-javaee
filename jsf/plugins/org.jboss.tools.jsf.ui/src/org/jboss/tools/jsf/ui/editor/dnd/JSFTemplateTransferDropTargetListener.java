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
package org.jboss.tools.jsf.ui.editor.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

public class JSFTemplateTransferDropTargetListener extends TemplateTransferDropTargetListener {

	public JSFTemplateTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
	}

	protected CreationFactory getFactory(Object template) {
		return new JSFTemplateFactory();
	}

	class JSFTemplateFactory implements CreationFactory {
		public Object getNewObject() {
			return "view"; //$NON-NLS-1$
		}

		public Object getObjectType() {
			return String.class;
		}
	}

}
