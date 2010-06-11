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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.results.ListResult;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarPushButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarRadioButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarSeparatorButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarToggleButton;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.hamcrest.Description;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;

public class ValidationUIEditorBot extends SWTBotEditor {

    private static final Logger L = Logger.getLogger(ValidationUIEditorBot.class.getName());

    public ValidationUIEditorBot(IEditorReference editorReference,
            SWTWorkbenchBot bot) throws WidgetNotFoundException {
        super(editorReference, bot);
    }

    /**
     * Gets the toolbar buttons currently visible.
     *
     * @return The set of toolbar buttons.
     */
    @Override
    public List<SWTBotToolbarButton> getToolbarButtons() {
        final ToolBar toolbar = bot().widget(new AbstractMatcher<ToolBar>() {

            @Override
            protected boolean doMatch(Object item) {
                return item instanceof ToolBar;
            }

            public void describeTo(Description description) {
            }
        });
        return UIThreadRunnable.syncExec(new ListResult<SWTBotToolbarButton>() {

            public List<SWTBotToolbarButton> run() {
                final List<SWTBotToolbarButton> l = new ArrayList<SWTBotToolbarButton>();
                if (toolbar == null) {
                    return l;
                }
                ToolItem[] items = toolbar.getItems();
                L.fine("number of items : " + items.length);
                for (int i = 0; i < items.length; i++) {
                    try {
                        if (SWTUtils.hasStyle(items[i], SWT.PUSH)) {
                            l.add(new SWTBotToolbarPushButton(items[i]));
                        } else if (SWTUtils.hasStyle(items[i], SWT.CHECK)) {
                            l.add(new SWTBotToolbarToggleButton(items[i]));
                        } else if (SWTUtils.hasStyle(items[i], SWT.RADIO)) {
                            l.add(new SWTBotToolbarRadioButton(items[i]));
                        } else if (SWTUtils.hasStyle(items[i], SWT.DROP_DOWN)) {
                            l.add(new SWTBotToolbarDropDownButton(items[i]));
                        } else if (SWTUtils.hasStyle(items[i], SWT.SEPARATOR)) {
                            l.add(new SWTBotToolbarSeparatorButton(items[i]));
                        }
                    } catch (WidgetNotFoundException e) {
                        L.log(Level.WARNING, "Failed to find widget " + items[i].getText(), e);
                    }
                }
                return l;
            }
        });
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
}
