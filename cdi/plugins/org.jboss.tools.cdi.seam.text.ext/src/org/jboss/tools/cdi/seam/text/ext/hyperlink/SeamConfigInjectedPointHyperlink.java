/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.seam.config.core.definition.IConfigDefinition;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtPlugin;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlink;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ITextSourceReference;

public class SeamConfigInjectedPointHyperlink extends InjectedPointHyperlink {

	public SeamConfigInjectedPointHyperlink(IRegion region, IBean bean, IDocument document) {
		super(region, bean, document);
	}

	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		ITextSourceReference source = ((AbstractBeanElement)bean).getDefinition().getOriginalDefinition();
		IFile resource = (IFile)source.getResource();
		IWorkbenchWindow window = CDISeamExtPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null)	return;
		IWorkbenchPage page = window.getActivePage();
		try {
			part = IDE.openEditor(page, resource);
		} catch (PartInitException e) {
			CDISeamExtPlugin.log(e);
		}
		if(part instanceof EditorPartWrapper) {
			part = ((EditorPartWrapper)part).getEditor();
		}
		if(part instanceof ObjectMultiPageEditor) {
			ObjectMultiPageEditor mpe = (ObjectMultiPageEditor)part;
			ITextEditor textEditor = (ITextEditor)mpe.getAdapter(ITextEditor.class);
			if(textEditor != null) {
				mpe.setActiveEditor(textEditor);
				part = textEditor;
			}
		}
		if(part != null) {
			part.getEditorSite().getSelectionProvider().setSelection(new TextSelection(source.getStartPosition(), source.getLength()));
		}
	}

	@Override
	public String getHyperlinkText() {
		String text = super.getHyperlinkText();
		IConfigDefinition configDef = (IConfigDefinition)((AbstractBeanElement)bean).getDefinition();
		int line = configDef.getConfig().getNode().getLocation().getLine();
		ITextSourceReference source = ((AbstractBeanElement)bean).getDefinition().getOriginalDefinition();
		text += " in " + source.getResource().getName() + " at line " + line;
		return text;
	}

}
