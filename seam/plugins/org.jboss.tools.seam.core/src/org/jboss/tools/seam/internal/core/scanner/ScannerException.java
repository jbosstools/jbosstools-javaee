/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core.scanner;

public class ScannerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ScannerException() {
	}
	
	public ScannerException(Throwable cause) {
		super(cause);
	}

	public ScannerException(String message, Throwable cause) {
		super(message, cause);
	}

}
