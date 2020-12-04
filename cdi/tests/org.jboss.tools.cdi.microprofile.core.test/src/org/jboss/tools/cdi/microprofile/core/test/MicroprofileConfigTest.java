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
package org.jboss.tools.cdi.microprofile.core.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;
import org.junit.Test;

/**
 * @author Red Hat Developers
 *
 */
public class MicroprofileConfigTest extends MicroprofileCoreTest {

  @Override
  String getProjectName() {
    return "microprofile-config";
  }
  
  @Test
  public void validateResource() throws CoreException {
    IFile file = project.getFile("src/main/java/org/wildfly/quickstarts/microprofile/config/ConfigResource.java");
    AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[0]);
  }
  
  @Test
  public void validateCustomerResource() throws CoreException {
    IFile file = project.getFile("src/main/java/org/wildfly/quickstarts/microprofile/config/CustomConfigResource.java");
    AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[0]);
  }

}
