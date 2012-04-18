 /*******************************************************************************
  * Copyright (c) 2007-2012 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.ui.bot.test.compatibility;

import java.io.File;
import java.io.IOException;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.gen.ActionItem.Import.OtherJSFProject;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.jboss.tools.ui.bot.ext.wizards.SWTBotImportWizard;
/**
 * Test importing JSF 1.2 project created using JBDS 4.1.x
 * @author Vladimir Pakan
 *
 */
public class ImportJSF12ProjectFromJBDS4x extends JSFAutoTestCase{
  
  private SWTBotExt botExt;	
  private static final String PROJECT_TO_IMPORT_NAME = "jsf12importtest";
  
  public void setUp() throws Exception {
    super.setUp();
    this.botExt = new SWTBotExt();
  }
  
	public void testImportJSFProject() {
    // copy and unzip JSF 1.2 project to tmp directory
    try{
      final String projectToImportZipName = ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME + ".zip";
      final String resourceProjectToImportLocation = getPathToResources("projects" + File.separator + projectToImportZipName);
      final String tmpDir = System.getProperty("java.io.tmpdir",".");
      FileHelper.copyFilesBinary(new File(resourceProjectToImportLocation),
          new File(tmpDir));
      FileHelper.unzipArchive(new File(tmpDir,projectToImportZipName),
        new File(tmpDir));
      final String projectToImportLocation = tmpDir + File.separator + ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME;
      new SWTBotImportWizard().open(OtherJSFProject.LABEL);
      bot.textWithLabel(OtherJSFProject.TEXT_WEBXML_LOCATION)
        .setText(projectToImportLocation
            + File.separator + "WebContent"
            + File.separator + "WEB-INF"
            + File.separator + "web.xml");
      bot.button(IDELabel.Button.NEXT).click();
      // do not deploy to Server
      try{
        SWTBotCheckBox chbServerRuntime = bot.checkBox(1);
        if (chbServerRuntime.isChecked()){
          chbServerRuntime.deselect();
        }
      } catch (WidgetNotFoundException wnfe){
        // do nothing
      } catch (IndexOutOfBoundsException ioobe){
        // do nothing
      }
      bot.button(IDELabel.Button.FINISH).click();
      util.waitForAll(Timing.time10S());
      // wait till Building workspace job is started
      util.waitForNonIgnoredJobs(Timing.time60S());
      // check if project is present within package explorer
      assertTrue("Imported project " + ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME + " is not present in Packag Explorer", 
          eclipse.isProjectInPackageExplorer(PROJECT_TO_IMPORT_NAME));
      SWTBotTreeItem[] errors = ProblemsView.getFilteredErrorsTreeItems(botExt, 
          null, 
          File.separator + ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME, 
          null,
          null);
      // check if imported project has no errors and no warnings
      assertTrue("There were these errors when importing "
          + ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME 
          + " project" 
          + SWTEclipseExt.getFormattedTreeNodesText(errors),
          errors == null || errors.length == 0);
      SWTBotTreeItem[] warnings = ProblemsView.getFilteredErrorsTreeItems(botExt, 
          null, 
          File.separator + ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME, 
          null,
          null);
      assertTrue("There were these warnings when importing "
          + ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME 
          + " project" 
          + SWTEclipseExt.getFormattedTreeNodesText(warnings),
          errors == null || errors.length == 0);
    }catch (IOException ioe){
      throw new RuntimeException("Unable to copy and unzip necessary files from plugin's resources directory",ioe);
    }catch (Exception e){
      throw new RuntimeException(e);
    }
	}

  @Override
  public void tearDown() throws Exception {
    if (eclipse.isProjectInPackageExplorer(PROJECT_TO_IMPORT_NAME)){
      packageExplorer.deleteProject(ImportJSF12ProjectFromJBDS4x.PROJECT_TO_IMPORT_NAME, true);  
    }
    super.tearDown();
  }
	
	@Override
	protected void closeUnuseDialogs() {
		// not used
	}

	@Override
	protected boolean isUnuseDialogOpened() {
		return false;
	}
	
}
