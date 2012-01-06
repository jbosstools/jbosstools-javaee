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
package org.jboss.tools.cdi.ui.marker;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;

/**
 * @author Daniel Azarov
 */
public class MakeFieldProtectedMarkerResolution implements IMarkerResolution2, TestableResolutionWithDialog{
	private static final String PUBLIC = "public";  //$NON-NLS-1$
	private static final String PROTECTED = "protected";  //$NON-NLS-1$

	private String label;
	private IField field;
	private IFile file;
	
	public MakeFieldProtectedMarkerResolution(IField field, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_FIELD_PROTECTED_MARKER_RESOLUTION_TITLE, new Object[]{field.getElementName()});
		this.field = field;
		this.file = file;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		internal_run(marker, false);
	}
	
	@Override
	public void runForTest(IMarker marker) {
		internal_run(marker, true);
	}
	
	private void internal_run(IMarker marker, boolean test){
		if(!test){
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			boolean cont = MessageDialog.openQuestion(shell, CDIUIMessages.QUESTION, CDIUIMessages.DECREASING_FIELD_VISIBILITY_MAY_CAUSE_COMPILATION_PROBLEMS);
			if(!cont)
				return;
		}
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			if(original == null) {
				return;
			}
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());

			CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			change.setEdit(edit);
			IBuffer buffer = compilationUnit.getBuffer();
			
			int flag = field.getFlags();
			
			String text = buffer.getText(field.getSourceRange().getOffset(), field.getSourceRange().getLength());

			int position = field.getSourceRange().getOffset();
			if((flag & Flags.AccPublic) != 0){
				position += text.indexOf(PUBLIC);
				TextEdit re = new ReplaceEdit(position, PUBLIC.length(), PROTECTED);
				edit.addChild(re);
				//buffer.replace(position, PUBLIC.length(), PROTECTED);
			}
			
			if(edit.hasChildren()){
				change.perform(new NullProgressMonitor());
				original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
			}
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

	@Override
	public String getDescription() {
		return label;
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}
}
