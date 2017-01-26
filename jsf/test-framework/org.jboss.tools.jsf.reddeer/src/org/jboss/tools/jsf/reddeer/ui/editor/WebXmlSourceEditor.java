/*******************************************************************************
 * Copyright (c) 2015-2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.reddeer.ui.editor;

import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;

/**
 * RedDeer implementation of web.xml file source editor
 * 
 * @author vlado pakan
 *
 */
public class WebXmlSourceEditor extends TextEditor {
	public WebXmlSourceEditor(ITextEditor textEditor) {
		super(textEditor);
	}
}