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
package org.jboss.tools.struts.ui.editor.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

import org.jboss.tools.struts.ui.editor.ActionTemplate;
import org.jboss.tools.struts.ui.editor.GlobalExceptionTemplate;
import org.jboss.tools.struts.ui.editor.GlobalForwardTemplate;
import org.jboss.tools.struts.ui.editor.PageTemplate;
import org.jboss.tools.struts.ui.editor.TemplateConstants;
//import org.eclipse.swt.dnd.TextTransfer;


public class StrutsTemplateTransferDropTargetListener
	extends TemplateTransferDropTargetListener 
{

public StrutsTemplateTransferDropTargetListener(EditPartViewer viewer) {
	super(viewer);
	//setTransfer(TextTransfer.getInstance());
}

protected CreationFactory getFactory(Object template) {
	if (template instanceof String)
		return new JSFTemplateFactory((String)template);
	else return null;
}

class JSFTemplateFactory implements CreationFactory{
	String template;
	
	public JSFTemplateFactory(String template){
		this.template = template;
	}
	
	public Object getNewObject(){
		return new String("view");
	}

	public Object getObjectType(){
		if(TemplateConstants.TEMPLATE_ACTION.equals(template))
			return ActionTemplate.class;
		else if(TemplateConstants.TEMPLATE_GLOBAL_FORWARD.equals(template))
			return GlobalForwardTemplate.class;
		else if(TemplateConstants.TEMPLATE_GLOBAL_EXCEPTION.equals(template))
			return GlobalExceptionTemplate.class;
		else if(TemplateConstants.TEMPLATE_PAGE.equals(template))
			return PageTemplate.class;
		else return String.class;
	}
}

}
