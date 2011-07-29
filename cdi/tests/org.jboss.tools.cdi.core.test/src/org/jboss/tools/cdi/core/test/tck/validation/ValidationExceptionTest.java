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

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Alexey Kazakov
 */
public class ValidationExceptionTest extends TestCase {

	private static ValidationExceptionLogger LOGGER;

	public static ValidationExceptionLogger initLogger() {
		LOGGER = new ValidationExceptionLogger();
		return LOGGER;
	}

	public void testExceptions() {
		Set<IStatus> exceptions = LOGGER.getExceptions();
		StringBuffer error = new StringBuffer("The following exceptions were thrown during project validation:");
		for (IStatus status : exceptions) {
			Throwable cause = status.getException().getCause();
			error.append("\r\n").append(status.toString()).append(": ").append(cause.toString()).append(": ").append(cause.getStackTrace()[0].toString());
		}
		assertTrue(error.toString(), exceptions.isEmpty());
	}
}