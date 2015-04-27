/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck11;

import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;

/**
 * @author Alexey Kazakov
 */
public class TCK11AnnotatedProjectNameProvider implements ITCKProjectNameProvider {

	private final static String MAIN_PROJECT_NAME = "tck1.1-annotated";
	private final static String[] PROJECT_NAMES = {"tck1.1-annotated-parent", MAIN_PROJECT_NAME, "tck1.1-annotated-child"};
	private final static String MAIN_PROJECT_PATH = "/projects/tck1.1-annotated";
	private final static String[] PROJECT_PATHS = {"/projects/tck1.1-annotated-parent", MAIN_PROJECT_PATH, "/projects/tck1.1-annotated-child"};

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.test.ITCKProjectNameProvider#getMainProjectName()
	 */
	@Override
	public String getMainProjectName() {
		return MAIN_PROJECT_NAME;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.test.ITCKProjectNameProvider#getProjectNames()
	 */
	@Override
	public String[] getProjectNames() {
		return PROJECT_NAMES;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.test.ITCKProjectNameProvider#getMainProjectPath()
	 */
	@Override
	public String getMainProjectPath() {
		return MAIN_PROJECT_PATH;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.test.ITCKProjectNameProvider#getProjectPaths()
	 */
	@Override
	public String[] getProjectPaths() {
		return PROJECT_PATHS;
	}
}