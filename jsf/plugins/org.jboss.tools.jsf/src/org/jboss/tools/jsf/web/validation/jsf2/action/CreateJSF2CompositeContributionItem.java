/*******************************************************************************
 * Copyright (c) 2007-2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation.jsf2.action;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class CreateJSF2CompositeContributionItem extends ActionContributionItem {

	public CreateJSF2CompositeContributionItem() {
		super(new CreateJSF2CompositeAction());
	}

	@Override
	public void fill(Menu parent, int index) {
		getAction().setText(JSFUIMessages.CreateJSF2Composite);
		super.fill(parent, index);
	}
}
