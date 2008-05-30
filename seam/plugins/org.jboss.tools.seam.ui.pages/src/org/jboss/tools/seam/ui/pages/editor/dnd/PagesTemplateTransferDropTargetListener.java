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
package org.jboss.tools.seam.ui.pages.editor.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;
import org.jboss.tools.seam.ui.pages.editor.ExceptionTemplate;
import org.jboss.tools.seam.ui.pages.editor.PageTemplate;
import org.jboss.tools.seam.ui.pages.editor.TemplateConstants;

public class PagesTemplateTransferDropTargetListener extends TemplateTransferDropTargetListener {

	public PagesTemplateTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
	}

	protected CreationFactory getFactory(Object template) {
		return new JSFTemplateFactory((String)template);
	}

	class JSFTemplateFactory implements CreationFactory {
		String template;

		JSFTemplateFactory(String template) {
			this.template = template;
		}

		public Object getNewObject() {
			return "view";
		}

		public Object getObjectType() {
			if(TemplateConstants.TEMPLATE_EXCEPTION.equals(template))
				return ExceptionTemplate.class;
			else if(TemplateConstants.TEMPLATE_PAGE.equals(template))
				return PageTemplate.class;
			else return String.class;
		}
	}

}
