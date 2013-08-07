/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck;

/**
 * @author Alexey Kazakov
 */
public interface ITCKProjectNameProvider {

//	private final static String MAIN_PROJECT_NAME = "tck";
//	private final static String[] PROJECT_NAMES = {"tck-parent", MAIN_PROJECT_NAME, "tck-child"};
//	private final static String MAIN_PROJECT_PATH = "/projects/tck";
//	private final static String[] PROJECT_PATHS = {"/projects/tck-parent", MAIN_PROJECT_PATH, "/projects/tck-child"};

	String getMainProjectName();
	String[] getProjectNames();
	String getMainProjectPath();
	String[] getProjectPaths();
}