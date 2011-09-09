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
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.seam.config.core.definition.IConfigDefinition;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlink;
import org.jboss.tools.common.text.ITextSourceReference;

public class SeamConfigInjectedPointHyperlink extends InjectedPointHyperlink {

	public SeamConfigInjectedPointHyperlink(IRegion region, IBean bean, IDocument document) {
		super(region, bean, document);
	}

	protected void doHyperlink(IRegion region) {
		bean.open();
	}

	@Override
	public String getHyperlinkText() {
		String text = super.getHyperlinkText();
		IConfigDefinition configDef = (IConfigDefinition)((AbstractBeanElement)bean).getDefinition();
		int line = configDef.getConfig().getNode().getLocation().getLine();
		ITextSourceReference source = ((AbstractBeanElement)bean).getDefinition().getOriginalDefinition();
		text += " in " + source.getResource().getName() + " at line " + line;
		return text;
	}

}
