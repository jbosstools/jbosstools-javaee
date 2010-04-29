/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.components;

import java.text.MessageFormat;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2URITempComponent implements IJSF2ValidationComponent {

	private int length;
	private int startOffSet;
	private int line;
	private String validationMessage = ""; //$NON-NLS-1$
	private Object[] messageParams;
	private String URI;

	public JSF2URITempComponent(String URI) {
		this.URI = URI;
	}

	public int getLength() {
		return length;
	}

	void setLength(int length) {
		this.length = length;
	}

	public int getLine() {
		return line;
	}

	void setLine(int lineNumber) {
		this.line = lineNumber;
	}

	public int getStartOffSet() {
		return startOffSet;
	}

	void setStartOffSet(int startOffSet) {
		this.startOffSet = startOffSet;
	}

	void createValidationMessage() {
		String mesParam = "/resources" + URI.replaceAll(JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$ //$NON-NLS-2$
		this.validationMessage = MessageFormat.format(
				JSFUIMessages.Missing_JSF_2_Resources_Folder, mesParam);
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public Object[] getMessageParams() {
		return messageParams;
	}

	void createMessageParams() {
		messageParams = new Object[] { this };
	}

	public String getType() {
		return JSF2_URI_TYPE;
	}

	public String getComponentResourceLocation() {
		return ""; //$NON-NLS-1$
	}

	public int getSeverity() {
		return IMessage.NORMAL_SEVERITY;
	}

	public String getURI() {
		return URI;
	}

}
