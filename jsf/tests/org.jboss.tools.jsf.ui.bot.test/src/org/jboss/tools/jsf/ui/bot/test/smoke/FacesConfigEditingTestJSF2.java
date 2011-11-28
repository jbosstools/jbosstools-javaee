/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.smoke;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
/** Test Editing of faces-config.xml file for JSF 1.2 project
 * @author Vladimir Pakan
 *
 */
public class FacesConfigEditingTestJSF2 extends AbstractFacesConfigEditingTest{
  
  @Override
  protected SWTBotEditor getFacesConfigEditor() {
    return eclipse.openFile(getTestProjectName(), 
        "WebContent",
        "WEB-INF",
        FacesConfigEditingTestJSF2.FACES_CONFIG_FILE_NAME);
  }
  @Override
  protected String getTestProjectName(){
    return JSF2_TEST_PROJECT_NAME;
  }
  @Override
  protected void intializeTestProject() {
    createJSF2Project(getTestProjectName());
  }
  @Override
  protected TestProjectType getTestProjectType() {
    return TestProjectType.JSF2;
  }
  @Override
  protected boolean getCheckForExistingManagedBeanClass() {
    return false;
  }
}
  
