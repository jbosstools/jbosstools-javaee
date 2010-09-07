/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.model.impl;

import org.jboss.tools.common.model.impl.*;

public class SystemEventListenerObjectImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 532550263999885851L;

	public String getPresentationString() {
		String listener = getAttributeValue("system-event-listener-class");
		if(listener != null && listener.length() > 0) {
			listener = listener.substring(listener.lastIndexOf('.') + 1);
		}
		String event = getAttributeValue("system-event-class");
		if(event != null && event.length() > 0) {
			event = event.substring(event.lastIndexOf('.') + 1);
		}
		return "" + event + " -> " + listener;
	}

	public String getPathPart() {
		String listener = getAttributeValue("system-event-listener-class");
		String event = getAttributeValue("system-event-class");
		return applyDuplicate("" + event + ":" + listener);
	}

}
