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
package org.jboss.tools.batch.ui.editor.internal.action;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.JumpActionHandler;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class OpenOrCreateArtifactActionHandler extends JumpActionHandler {

	@Override
	protected Object run(Presentation context) {
		OpenOrCreateArtifactActionDelegate delegate = new OpenOrCreateArtifactActionDelegate(context.part());
		if(delegate.getBatchProject() == null) {
			return null;
		}
		delegate.run();
		return null;
	}

}
