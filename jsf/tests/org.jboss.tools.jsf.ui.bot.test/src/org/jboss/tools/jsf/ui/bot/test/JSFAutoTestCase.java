package org.jboss.tools.jsf.ui.bot.test;

import java.io.IOException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.ui.bot.test.WidgetVariables;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;

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
		return FileLocator
				.toFileURL(
						Platform.getBundle(Activator.PLUGIN_ID).getEntry("/")).getFile() + "resources/" + testPage; //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void openTestPage() {
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		tree.expandNode(JBT_TEST_PROJECT_NAME)
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
	protected void tearDown() throws Exception {

		// Restore page state before tests
		if (editor != null) {
			editor.setFocus();
			bot.menu("Edit").menu("Select All").click(); //$NON-NLS-1$ //$NON-NLS-2$
			bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$ //$NON-NLS-2$
			editor.setText(editorText);
			editor.save();
		}
		super.tearDown();
	}

}
