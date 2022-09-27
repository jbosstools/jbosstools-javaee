/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.faces.core.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.junit.Before;

/**
 * @author Red Hat Developers
 *
 */
public abstract class FacesCoreTest {
  
  private static final String PLUGIN_ID = "org.jboss.tools.cdi.faces.core.test";
  
  protected IProject project;
  
  @Before
  public void setUp() throws InvocationTargetException, CoreException, IOException, InterruptedException {
    project = importProject(getProjectName());
  }
  
  /**
   * @param projectName
   * @throws CoreException 
   * @throws InterruptedException 
   * @throws IOException 
   * @throws InvocationTargetException 
   */
  protected IProject importProject(String projectName) throws CoreException, InvocationTargetException, IOException, InterruptedException {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(new Path(projectName).lastSegment());
    if(!project.exists()) {
      project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/" + projectName);
      TestUtil._waitForValidation(project);
    }
    return project;
  }

  abstract String getProjectName();

}
