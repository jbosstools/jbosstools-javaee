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

package org.jboss.tools.seam.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;

/**
 * @author eskimo
 *
 */
public class SeamBaseWizardPage extends WizardPage implements IAdaptable {

	/**
	 * 
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamBaseWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @param pageName
	 */
	protected SeamBaseWizardPage(String pageName) {
		super(pageName);

	}

	Map<String,IFieldEditor> editorRegistry = new HashMap<String,IFieldEditor>();
	
	List<IFieldEditor> editorOrder = new ArrayList<IFieldEditor>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		setControl(new GridLayoutComposite(parent));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(adapter == Map.class)
			return editorRegistry;
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @param editor
	 */
	public void addEditor(IFieldEditor editor) {
		editorRegistry.put(editor.getName(), editor);
		editorOrder.add(editor);
	}
	/**
	 * 
	 * @param id
	 * @param editor
	 */
	public void addEditors(IFieldEditor[] editors) {
		for (IFieldEditor fieldEditor : editors) {
			editorRegistry.put(fieldEditor.getName(), fieldEditor);
			editorOrder.add(fieldEditor);
		}
	}
	
	/**
	 * 
	 * @author eskimo
	 *
	 */
	public class GridLayoutComposite extends Composite {

		public GridLayoutComposite(Composite parent, int style) {
			super(parent, style);
			int columnNumber = 1;
			for (IFieldEditor fieldEditor : editorOrder) {
				if(fieldEditor.getNumberOfControls()>columnNumber)
					columnNumber=fieldEditor.getNumberOfControls();
			}
			GridLayout gl = new GridLayout(columnNumber,false);
			setLayout(gl);
			for (IFieldEditor fieldEditor2 : editorOrder) {
				fieldEditor2.doFillIntoGrid(this);
			}
		}
		
		public GridLayoutComposite(Composite parent) {
			this(parent, SWT.NONE);
			
		}
	}
}
