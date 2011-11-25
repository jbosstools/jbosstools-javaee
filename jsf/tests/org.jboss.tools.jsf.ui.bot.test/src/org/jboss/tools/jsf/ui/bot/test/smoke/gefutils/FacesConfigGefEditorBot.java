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
package org.jboss.tools.jsf.ui.bot.test.smoke.gefutils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.part.EditorPart;

/**
 * modified copy of SWTBotGefEditor class to get it working with Faces Config GEF editor
 * 
 * @author Vlado Pakan
 */
public class FacesConfigGefEditorBot extends SWTBotEditor {

	private final FacesConfigGefEditorViewer viewer;
  private static final Logger log = Logger.getLogger(FacesConfigGefEditorBot.class.getName());

	public FacesConfigGefEditorBot(final IEditorReference editorReference)
			throws WidgetNotFoundException {
		super(editorReference, new SWTWorkbenchBot());
		GraphicalViewer graphicalViewer = UIThreadRunnable.syncExec(new Result<GraphicalViewer>() {
			public GraphicalViewer run() {
				final IEditorPart editor = partReference.getEditor(true);
                EditorPart ep = null;
                try {
                    Field f = editor.getClass().getDeclaredField("editor");
                    f.setAccessible(true);
                    Object/*StrutsConfigEditor*/ o = f.get(editor);
                    f.setAccessible(false);
                    f = o.getClass().getDeclaredField("guiEditor");
                    f.setAccessible(true);
                    Object o2 = f.get(o);
                    f.setAccessible(false);
                    Method m = o2.getClass().getMethod("getGUI");
                    ep = (EditorPart) m.invoke(o2);
                } catch (SecurityException e) {
                    log.log(Level.WARNING, e.getMessage(), e);
                } catch (NoSuchFieldException e) {
                  log.log(Level.WARNING, e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                  log.log(Level.WARNING, e.getMessage(), e);
                } catch (IllegalAccessException e) {
                  log.log(Level.WARNING, e.getMessage(), e);
                } catch (NoSuchMethodException e) {
                  log.log(Level.WARNING, e.getMessage(), e);
                } catch (InvocationTargetException e) {
                  log.log(Level.WARNING, e.getMessage(), e);
                }
				return (GraphicalViewer) ep.getAdapter(GraphicalViewer.class);
			}
		});
		viewer = new FacesConfigGefEditorViewer(graphicalViewer);
	}
  
	public SWTBotGefViewer getViewer(){
	  return viewer;
	}
	
	public Control getControl(){
    return viewer.getControl();
  }
	
  private static class FacesConfigGefEditorViewer extends SWTBotGefViewer {

    public FacesConfigGefEditorViewer(GraphicalViewer graphicalViewer)
        throws WidgetNotFoundException {
      super(graphicalViewer);
    }

    public Control getControl() {
      return super.getControl();
    }

  }
}
