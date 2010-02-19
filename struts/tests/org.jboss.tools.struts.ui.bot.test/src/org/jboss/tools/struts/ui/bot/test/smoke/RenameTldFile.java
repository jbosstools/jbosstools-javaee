 /*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.struts.ui.bot.test.smoke;

import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.FileRenameHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;

/**
 * Test renaming of Tld file
 * @author Vladimir Pakan
 *
 */
public class RenameTldFile extends SWTTestExt{
  private static final String OLD_TLD_FILE_NAME = "struts-html.tld";
  private static final String NEW_TLD_FILE_NAME = "struts-html-renamed.tld";
  /**
   * Test renaming of struts-config.xml file
   */
  @Test
	public void testRenameTldFile() {
    String checkResult = FileRenameHelper.checkFileRenamingWithinWebProjects(bot,
        OLD_TLD_FILE_NAME, NEW_TLD_FILE_NAME,
        new String[]{StrutsAllBotTests.STRUTS_PROJECT_NAME, IDELabel.WebProjectsTree.TAG_LIBRARIES},
        " [html]");
    assertNull(checkResult, checkResult);
  }
	
}
