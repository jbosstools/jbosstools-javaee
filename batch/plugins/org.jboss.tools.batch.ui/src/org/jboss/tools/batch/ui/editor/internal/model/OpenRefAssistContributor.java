/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContribution;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.jboss.tools.batch.ui.editor.internal.action.OpenOrCreateArtifactActionDelegate;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class OpenRefAssistContributor extends PropertyEditorAssistContributor {

	public OpenRefAssistContributor() {
		setPriority(130);   	
	}

	@Override
	public void contribute(PropertyEditorAssistContext context) {
		OpenOrCreateArtifactActionDelegate delegate = new OpenOrCreateArtifactActionDelegate(context.getPart());
		if(delegate.getBatchProject() == null) {
			return;
		}
		String actionText = delegate.getActionLabel();
		PropertyEditorAssistContribution.Factory contribution = PropertyEditorAssistContribution.factory();
		contribution.text( "<p><a href=\"action\" nowrap=\"true\">" + escapeForXml(actionText) + "</a></p>");
		contribution.link("action", delegate);
		context.getSection(SECTION_ID_ACTIONS).addContribution(contribution.create());
	}

}
