 /*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.cdi.ui.refactoring;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.internal.core.refactoring.RenameNamedBeanProcessor;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.refactoring.JBDSRefactoringWizard;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;

/**
 * @author Daniel Azarov
 */
public class RenameNamedBeanWizard extends JBDSRefactoringWizard {

	private IBean bean;
	private String componentName;
	private IFieldEditor editor;

	public RenameNamedBeanWizard(Refactoring refactoring, IBean bean) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
		this.bean = bean;
		if(bean != null){
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
	 */
	@Override
	protected void addUserInputPages() {
	    setDefaultPageTitle(getRefactoring().getName());
	    RenameNamedBeanProcessor processor= (RenameNamedBeanProcessor) getRefactoring().getAdapter(RenameNamedBeanProcessor.class);
	    addPage(new RenameNamedBeanWizardPage(processor));
	}
	
	class RenameNamedBeanWizardPage extends UserInputWizardPage{
		private RenameNamedBeanProcessor processor;
		
		public RenameNamedBeanWizardPage(RenameNamedBeanProcessor processor){
			super("");
			this.processor = processor;
		}

		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        GridLayout layout = new GridLayout();
	        container.setLayout(layout);
	        layout.numColumns = 2;
	        
	        String defaultName = bean.getName();
	        editor = IFieldEditorFactory.INSTANCE.createTextEditor(componentName, CDIUIMessages.RENAME_NAMED_BEAN_WIZARD_FIELD_NAME, defaultName);
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