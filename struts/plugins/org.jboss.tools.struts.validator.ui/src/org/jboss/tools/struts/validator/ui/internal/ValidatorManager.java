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
package org.jboss.tools.struts.validator.ui.internal;

import org.jboss.tools.struts.validator.ui.internal.ValidatorCommand;

public class ValidatorManager {
	private static ValidatorManager manager;

	public static ValidatorManager getDefault() {
		return manager;
	}
	
	public void registerValidators(Object validator) {
	}
	
	public void unregisterValidators(Object validator) {
	}
	
	public ValidatorCommand getCommand(int cmd, Object obj) {
		return null;
	}
	
	public void setCommand(ValidatorCommand cmd) {
	}
}
