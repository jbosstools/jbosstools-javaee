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
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.IFilterable;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlink;

public class GenericInjectedPointHyperlink extends InjectedPointHyperlink implements IFilterable{

	public GenericInjectedPointHyperlink(IRegion region, IBean bean, IDocument document) {
		super(region, bean, document);
	}

	@Override
	public String getHyperlinkText() {
		String text="";
		if (bean != null) {
			String str = bean.getSimpleJavaName();
			String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
			String beanPackage = beanTypeName.substring(0,beanTypeName.lastIndexOf("."));
			return str+" - "+beanPackage;
		}
		return text;
	}

	public String getFullyQualifiedName() {
		return bean.getBeanClass().getFullyQualifiedName();
	}

}
