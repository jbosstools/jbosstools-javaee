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
package org.jboss.tools.jsf.model.handlers.refactoring;

import org.eclipse.core.runtime.*;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.common.model.refactoring.ModelRenameProcessor;

public abstract class JSFRenameProcessor  extends ModelRenameProcessor {
		
	public JSFRenameProcessor() {}
	
	public String[] getAffectedProjectNatures() throws CoreException {
		return new String[]{JSFNature.NATURE_ID};
	}

}
