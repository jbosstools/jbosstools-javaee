/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.validation.KBValidationException;

/**
 * @author Alexey Kazakov
 */
public class ValidationExceptionTestSuite extends TestSuite implements ILogListener {

	private static Map<String, Set<IStatus>> EXCEPTIONS = new HashMap<String, Set<IStatus>>();

	public ValidationExceptionTestSuite(String name) {
		super(name);
		Platform.addLogListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime.IStatus, java.lang.String)
	 */
	public void logging(IStatus status, String plugin) {
		Set<IStatus> statuses = EXCEPTIONS.get(plugin);
		if(statuses==null) {
			statuses = new HashSet<IStatus>();
			EXCEPTIONS.put(plugin, statuses);
		}
		statuses.add(status);
	}

	public static boolean hasExceptions() {
		return !getExceptions().isEmpty();
	}

	public static Set<IStatus> getExceptions() {
		Set<IStatus> result = new HashSet<IStatus>();
		Set<IStatus> statuses = EXCEPTIONS.get(WebKbPlugin.PLUGIN_ID);
		for (IStatus status : statuses) {
			Throwable exception = status.getException();
			if(exception instanceof KBValidationException) {
				result.add(status);
			}
		}
		return result;
	}
}