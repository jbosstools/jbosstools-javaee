/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.util.model;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class TextProxy extends NodeProxy implements IDOMText {

	public TextProxy(IDOMText basicText, int basicOffset) {
		super(basicText, basicOffset);
		// TODO Auto-generated constructor stub
	}

	public void appendText(Text text) {
		((IDOMText) basicNode).appendText(text);

	}

	public String getWholeText() {
		return ((IDOMText) basicNode).getWholeText();
	}

	public boolean isElementContentWhitespace() {
		return ((IDOMText) basicNode).isElementContentWhitespace();
	}

	public boolean isInvalid() {
		return ((IDOMText) basicNode).isInvalid();
	}

	public Text replaceWholeText(String content) throws DOMException {
		return ((IDOMText) basicNode).replaceWholeText(content);
	}

	public Text splitText(int offset) throws DOMException {
		return ((IDOMText) basicNode).splitText(offset);
	}

	public void appendData(String arg) throws DOMException {
		((IDOMText) basicNode).appendData(arg);

	}

	public void deleteData(int offset, int count) throws DOMException {
		((IDOMText) basicNode).deleteData(offset, count);

	}

	public String getData() throws DOMException {
		return ((IDOMText) basicNode).getData();
	}

	public void insertData(int offset, String arg) throws DOMException {
		((IDOMText) basicNode).insertData(offset, arg);

	}

	public void replaceData(int offset, int count, String arg)
			throws DOMException {
		((IDOMText)basicNode).replaceData(offset, count, arg);

	}

	public void setData(String data) throws DOMException {
		((IDOMText)basicNode).setData(data);
		
	}

	public String substringData(int offset, int count) throws DOMException {
		return ((IDOMText) basicNode).substringData(offset, count);
	}

}
