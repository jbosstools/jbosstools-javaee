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

package org.jboss.tools.seam.ui.wizard;

/**
 * @author eskimo
 *
 */
public class SeamConversationVizardPage1 extends SeamBaseWizardPage {

	public SeamConversationVizardPage1() {
		super("seam.new.conversation.page1","Seam Conversation",null);
		setMessage("Select the name of the new Seam Conversation. A set of classes " +
				"managing a coversation will be created.");
	}
}
