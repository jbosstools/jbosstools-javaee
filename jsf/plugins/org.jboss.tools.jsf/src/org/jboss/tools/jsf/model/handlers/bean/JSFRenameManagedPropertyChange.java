/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.handlers.bean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.internal.corext.refactoring.changes.*;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.impl.XModelObjectImpl;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class JSFRenameManagedPropertyChange extends TextFileChange {
	private XModelObject beanProperty;
	private String newName;
	XModel model;
	boolean ok = false;
	
	public static JSFRenameManagedPropertyChange createChange(XModelObject beanProperty, String newName) {
		String name = beanProperty.getPresentationString();
		IFile f = getFile(beanProperty);
		if(f == null) return null;
		return new JSFRenameManagedPropertyChange(name, f, beanProperty, newName);
	}
	
	private JSFRenameManagedPropertyChange(String name, IFile file, XModelObject beanProperty, String newName) {
		super(name, file);
		this.beanProperty = beanProperty;
		this.newName = newName;
		model = beanProperty.getModel();
		addEdits();
	}
	
	void addEdits() {
		PositionSearcher searcher = new PositionSearcher();
		XModelObject o = ((XModelObjectImpl)beanProperty).getResourceAncestor();
		String text = ((FileAnyImpl)o).getAsText();
		searcher.init(text, beanProperty, "property-name"); //$NON-NLS-1$
		searcher.execute();
		int bp = searcher.getStartPosition();
		int ep = searcher.getEndPosition();
		ok = false;
		if(bp >= 0 && ep >= ep) {
			ReplaceEdit edit = new ReplaceEdit(bp, ep - bp, newName);
			TextChangeCompatibility.addTextEdit(this, JSFUIMessages.UPDATE_FIELD_REFERENCE, edit);
			ok = true;
		}
	}
	
	private static IFile getFile(XModelObject beanProperty) {
		XModelObject o = ((XModelObjectImpl)beanProperty).getResourceAncestor();
		return o == null ? null : (IFile)EclipseResourceUtil.getResource(o);
	}

	public String getName() {
		return JSFUIMessages.MANAGED_PROPERTY_RENAME;
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		if(ok) {
			return super.perform(pm);
		}
		if(beanProperty != null) {
			beanProperty.getModel().changeObjectAttribute(beanProperty, "property-name", newName); //$NON-NLS-1$
		}
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}
	
}
