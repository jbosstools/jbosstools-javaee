package org.jboss.tools.jsf.ui.bot.test;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.ui.bot.test.WidgetVariables;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
import org.jboss.tools.vpe.ui.bot.test.tools.SWTBotWebBrowser;

public abstract class JSFAutoTestCase extends VPEAutoTestCase {

	private String editorText;
	private SWTBotEclipseEditor editor;

	String getEditorText() {
		return editorText;
	}

	protected void setEditorText(String textEditor) {
		this.editorText = textEditor;
	}

	protected SWTBotEclipseEditor getEditor() {
		return editor;
	}

	protected void setEditor(SWTBotEclipseEditor editor) {
		this.editor = editor;
	}

	@Override
	protected String getPathToResources(String testPage) throws IOException {
		String filePath = FileLocator
				.toFileURL(
						Platform.getBundle(Activator.PLUGIN_ID).getEntry("/")).getFile() + "resources/" + testPage; //$NON-NLS-1$ //$NON-NLS-2$

		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			filePath = FileLocator
					.toFileURL(
							Platform.getBundle(Activator.PLUGIN_ID).getEntry(
									"/")).getFile() + testPage; //$NON-NLS-1$ 
		}
		return filePath;
	}

	protected void openTestPage() {
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		tree
				.expandNode(JBT_TEST_PROJECT_NAME)
				.expandNode("WebContent").expandNode("pages").getNode(TEST_PAGE).doubleClick(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void checkVPE(String testPage) throws Throwable {
		waitForBlockingJobsAcomplished(VISUAL_UPDATE);
		performContentTestByDocument(testPage, bot
				.multiPageEditorByTitle(TEST_PAGE));
	}

	@Override
	protected void closeUnuseDialogs() {
	}

	@Override
	protected boolean isUnuseDialogOpened() {
		return false;
	}

	@Override
	public void tearDown() throws Exception {

		// Restore page state before tests
		if (editor != null) {
			editor.setFocus();
			bot.menu("Edit").menu("Select All").click(); //$NON-NLS-1$ //$NON-NLS-2$
			bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$ //$NON-NLS-2$
			editor.setText(editorText);
			editor.save();
			delay();
		}
		super.tearDown();
	}

	protected String loadFileContent(String resourceRelativePath) throws IOException {
		File file = new File(getPathToResources(resourceRelativePath));
		StringBuilder builder = new StringBuilder(""); //$NON-NLS-1$
		Scanner scanner = null;
    try {
      scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        builder.append(scanner.nextLine() + "\n"); //$NON-NLS-1$
      }
    } 
    catch (IllegalStateException e) {
      if (scanner != null) {
        scanner.close();
      }
    }
    catch (NoSuchElementException e) {
      if (scanner != null) {
        scanner.close();
      }
    }
    return builder.toString();
	}
  /**
   * Returns CSS Editor text striped from spaces, tabs CR and EOL
   * @param editorText
   * @return String
   */
  protected static String stripCSSText(String editorText){
    return editorText.replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "").replaceAll(" ", "");
  }
  /**
   * Asserts if Visual Editor contains node with value valueToContain
   * @param webBrowser
   * @param valueToContain
   * @param fileName
   */
  protected static void assertVisualEditorContainsNodeWithValue (SWTBotWebBrowser webBrowser,
      String valueToContain,
      String fileName){
    assertTrue("Visual Representation of file " + fileName
        + " has to contain node with "
        + valueToContain
        + " value but it doesn't",
        webBrowser.containsNodeWithValue(webBrowser,
            valueToContain));
    
  }
  
  /**
   * Asserts if Visual Editor doesn't contain node with particular attributes
   * @param webBrowser
   * @param valueToContain
   * @param fileName
   */
  protected static void assertVisualEditorNotContainNodeWithValue (SWTBotWebBrowser webBrowser,
      String valueToContain,
      String fileName){
    
    assertFalse("Visual Representation of file " + fileName
        + " cannot contain node with "
        + valueToContain
        + " value but it does",
        webBrowser.containsNodeWithValue(webBrowser, 
            valueToContain));
    
  }
}
