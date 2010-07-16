 /*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.ui.el.refactoring;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.jsf.el.refactoring.RenameELVariableProcessor;
import org.jboss.tools.jsf.el.refactoring.RenameMessagePropertyProcessor;
import org.jboss.tools.jsf.ui.JsfUIMessages;

/**
 * @author Daniel Azarov
 */
public class RenameMessagePropertyWizard extends RefactoringWizard {

	private String propertyName;
	private IFieldEditor editor;

	public RenameMessagePropertyWizard(Refactoring refactoring, IFile editorFile) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
	 */
	@Override
	protected void addUserInputPages() {
	    setDefaultPageTitle(getRefactoring().getName());
	    RenameMessagePropertyProcessor processor= (RenameMessagePropertyProcessor) getRefactoring().getAdapter(RenameMessagePropertyProcessor.class);
	    addPage(new RenameMessagePropertyWizardPage(processor));
	}
	
	class RenameMessagePropertyWizardPage extends UserInputWizardPage{
		private RenameMessagePropertyProcessor processor;
		
		public RenameMessagePropertyWizardPage(RenameMessagePropertyProcessor processor){
			super("");
			this.processor = processor;
			propertyName = processor.getOldName();
		}

		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        GridLayout layout = new GridLayout();
	        container.setLayout(layout);
	        layout.numColumns = 2;
	        
	        String defaultName = propertyName;
	        editor = FieldEditorFactory.createTextEditor(propertyName, JsfUIMessages.RENAME_MESSAGE_PROPERTY_WIZARD_PROPERTY_NAME, defaultName);
	        editor.doFillIntoGrid(container);
	        
	        ((CompositeEditor)editor).addPropertyChangeListener(new PropertyChangeListener(){
	        	public void propertyChange(PropertyChangeEvent evt){
	        		validatePage();
	        	}
	        });
	        setControl(container);
	        setPageComplete(false);
		}
		
		protected final void validatePage() {
			RefactoringStatus status= new RefactoringStatus();
			setPageComplete(status);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ltk.ui.refactoring.UserInputWizardPage#performFinish()
		 */
		protected boolean performFinish() {
			
			initializeRefactoring();
			return super.performFinish();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ltk.ui.refactoring.UserInputWizardPage#getNextPage()
		 */
		public IWizardPage getNextPage() {
			initializeRefactoring();
			return super.getNextPage();
		}
		
		private void initializeRefactoring() {
			processor.setNewName(editor.getValueAsString());
		}
		
	}
}