/******************************************************************************* 
 * Copyright (c) 2011-2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class BatchPropertyHyperlink extends AbstractHyperlink{
	protected IBatchProperty batchProperty;
	
	public BatchPropertyHyperlink(IRegion region, IBatchProperty batchProperty, IDocument document){
		this.batchProperty = batchProperty;
		setRegion(region);
		setDocument(document);
	}

	protected void doHyperlink(IRegion region) {
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		BatchPropertyDialog dialog = new BatchPropertyDialog(display.getActiveShell(), batchProperty);
		dialog.open();
	}

	@Override
	public String getHyperlinkText() {
		return BatchHyperlinkMessages.SHOW_ALL_BATCH_PROPERTY_REFERENCES;
	}
	
}
