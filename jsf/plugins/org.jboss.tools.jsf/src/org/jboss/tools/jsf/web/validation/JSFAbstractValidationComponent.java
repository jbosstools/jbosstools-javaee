/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSFValidationComponent;

public abstract class JSFAbstractValidationComponent implements
		IJSFValidationComponent {

	private int length;
	private int startOffSet;
	private int line;
	private Object[] messageParams;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int lineNumber) {
		this.line = lineNumber;
	}
	
	public int getStartOffSet() {
		return startOffSet;
	}

	public void setStartOffSet(int startOffSet) {
		this.startOffSet = startOffSet;
	}

	public Object[] getMessageParams() {
		return messageParams;
	}

	public int getSeverity() {
		return IMessage.NORMAL_SEVERITY;
	}
	
	public abstract void createValidationMessage();
	
	public void createMessageParams() {
		this.messageParams = new Object[] { this };
	}

}
