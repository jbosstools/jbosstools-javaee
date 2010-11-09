/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.struts.ui.bot.test.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;

public class StrutsUIEditorBot extends SWTBotGefEditor {

    private static final Logger L = Logger.getLogger(StrutsUIEditorBot.class.getName());

    public StrutsUIEditorBot(IEditorReference reference) throws WidgetNotFoundException {
        super(reference, new SWTWorkbenchBot());
    }

//    @Override
//    protected void init() throws WidgetNotFoundException {
//        UIThreadRunnable.syncExec(new VoidResult() {
//
//            public void run() {
//                final IEditorPart/*EditorPartWrapper*/ editor = partReference.getEditor(true);
//                EditorPart/*StrutsEditor*/ ep = null;
//                try {
//                    Field f = editor.getClass().getDeclaredField("editor");
//                    f.setAccessible(true);
//                    Object/*StrutsConfigEditor*/ o = f.get(editor);
//                    f.setAccessible(false);
//                    f = o.getClass().getDeclaredField("guiEditor");
//                    f.setAccessible(true);
//                    Object/*StrutsConfigGuiEditor*/ o2 = f.get(o);
//                    f.setAccessible(false);
//                    Method m = o2.getClass().getMethod("getGUI");
//                    ep = (EditorPart) m.invoke(o2);
//                } catch (SecurityException e) {
//                    L.log(Level.WARNING, e.getMessage(), e);
//                } catch (NoSuchFieldException e) {
//                    L.log(Level.WARNING, e.getMessage(), e);
//                } catch (IllegalArgumentException e) {
//                    L.log(Level.WARNING, e.getMessage(), e);
//                } catch (IllegalAccessException e) {
//                    L.log(Level.WARNING, e.getMessage(), e);
//                } catch (NoSuchMethodException e) {
//                    L.log(Level.WARNING, e.getMessage(), e);
//                } catch (InvocationTargetException e) {
//                    L.log(Level.WARNING, e.getMessage(), e);
//                }
//                graphicalViewer = (GraphicalViewer) ep.getAdapter(GraphicalViewer.class);
//                final Control control = graphicalViewer.getControl();
//                if (control instanceof FigureCanvas) {
//                    canvas = new SWTBotGefFigureCanvas((FigureCanvas) control);
//                }
//                editDomain = graphicalViewer.getEditDomain();
//            }
//        });
//
//        if (graphicalViewer == null) {
//            throw new WidgetNotFoundException("Editor does not adapt to a GraphicalViewer");
//        }
//    }

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

    public Control/*FigureCanvas*/ getControl() {
    	return null;
//        return UIThreadRunnable.syncExec(new Result<Control>() {
//
//            public Control run() {
//                return graphicalViewer.getControl();
//            }
//        });
    }
}
