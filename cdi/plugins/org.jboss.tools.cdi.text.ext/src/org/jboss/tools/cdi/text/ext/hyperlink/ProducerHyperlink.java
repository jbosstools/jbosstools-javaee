/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.text.ext.hyperlink;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;

public class ProducerHyperlink extends DisposerHyperlink{
	
	public ProducerHyperlink(IRegion region, IMethod method, IDocument document){
		super(region, method, document);
	}

	@Override
	public String getHyperlinkText() {
		String text = CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_PRODUCER+" ";
		if(method != null)
			text += method.getElementName();
		return text;
	}
	
}
