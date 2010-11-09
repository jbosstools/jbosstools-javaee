package org.jboss.tools.struts.ui.bot.test.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;

/**
 * modified copy of SWTBotGefEditor class to get it working with Struts UI editor
 * 
 * @author jlukas
 */
public class StrutsUIEditorBot extends SWTBotEditor {

	private final StrutsUIEditorViewer viewer;
    private static final Logger L = Logger.getLogger(StrutsUIEditorBot.class.getName());

	public StrutsUIEditorBot(final IEditorReference editorReference)
			throws WidgetNotFoundException {
		super(editorReference, new SWTWorkbenchBot());
		GraphicalViewer graphicalViewer = UIThreadRunnable.syncExec(new Result<GraphicalViewer>() {
			public GraphicalViewer run() {
				final IEditorPart/*EditorPartWrapper*/ editor = partReference.getEditor(true);
                EditorPart/*StrutsEditor*/ ep = null;
                try {
                    Field f = editor.getClass().getDeclaredField("editor");
                    f.setAccessible(true);
                    Object/*StrutsConfigEditor*/ o = f.get(editor);
                    f.setAccessible(false);
                    f = o.getClass().getDeclaredField("guiEditor");
                    f.setAccessible(true);
                    Object/*StrutsConfigGuiEditor*/ o2 = f.get(o);
                    f.setAccessible(false);
                    Method m = o2.getClass().getMethod("getGUI");
                    ep = (EditorPart) m.invoke(o2);
                } catch (SecurityException e) {
                    L.log(Level.WARNING, e.getMessage(), e);
                } catch (NoSuchFieldException e) {
                    L.log(Level.WARNING, e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    L.log(Level.WARNING, e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    L.log(Level.WARNING, e.getMessage(), e);
                } catch (NoSuchMethodException e) {
                    L.log(Level.WARNING, e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    L.log(Level.WARNING, e.getMessage(), e);
                }
				return (GraphicalViewer) ep.getAdapter(GraphicalViewer.class);
			}
		});
		viewer = new StrutsUIEditorViewer(graphicalViewer);
	}
	
	public StrutsUIEditorBot activateTool(final String label) throws WidgetNotFoundException {
		viewer.activateTool(label);
		return this;
	}

	public StrutsUIEditorBot activateTool(final String label, int index) throws WidgetNotFoundException {
		viewer.activateTool(label, index);
		return this;
	}
	
	public SWTBotGefEditPart mainEditPart() throws WidgetNotFoundException {
		return viewer.mainEditPart();
	}
	
	public Control getControl() {
		return viewer.getControl();
	}

	public StrutsUIEditorBot clickContextMenu(String text) throws WidgetNotFoundException {
		viewer.clickContextMenu(text);
		return this;
	}
	
	public void selectPage(final String page) {
		IWorkbenchPart p = ((EditorPartWrapper) (getReference().getPart(true))).getEditor();
		assert p instanceof ObjectMultiPageEditor;
		final ObjectMultiPageEditor editor = (ObjectMultiPageEditor) p;
		// Select page
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				editor.selectPageByName(page);
			}
		});
	}

	private static class StrutsUIEditorViewer extends SWTBotGefViewer {

		public StrutsUIEditorViewer(GraphicalViewer graphicalViewer)
				throws WidgetNotFoundException {
			super(graphicalViewer);
		}
		
		public Control getControl() {
			return super.getControl();
		}
		
	}
}
