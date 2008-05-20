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
package org.jboss.tools.jsf.project.capabilities;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

public class PerformerContext {
	IProgressMonitor monitor;
	ArrayList<String> changeList;
	
	public PerformerContext(IProgressMonitor monitor) {
		this.monitor = monitor;
		changeList = new ArrayList<String>();
	}

}
