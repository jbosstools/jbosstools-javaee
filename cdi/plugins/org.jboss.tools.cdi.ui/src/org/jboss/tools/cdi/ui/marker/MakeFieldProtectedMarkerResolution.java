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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.CommonPlugin;
import org.jboss.tools.common.quickfix.IQuickFix;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
import org.jboss.tools.common.refactoring.TestableResolutionWithDialog;
import org.jboss.tools.common.ui.CommonUIPlugin;

/**
 * @author Daniel Azarov
 */
public class MakeFieldProtectedMarkerResolution implements IQuickFix, TestableResolutionWithDialog{
	private static final String PUBLIC = "public";  //$NON-NLS-1$
	private static final String PROTECTED = "protected";  //$NON-NLS-1$

	private String label;
	private IField field;
	private String description;
	private ICompilationUnit cUnit;
	
	public MakeFieldProtectedMarkerResolution(IField field){
		cUnit = CDIMarkerResolutionUtils.getJavaMember(field).getCompilationUnit();
		this.label = MessageFormat.format(CDIUIMessages.MAKE_FIELD_PROTECTED_MARKER_RESOLUTION_TITLE, new Object[]{field.getElementName()});
		this.field = field;
		description = getPreview();
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		do_run(false, false);
	}
	
	@Override
	public void runForTest(IMarker marker) {
		do_run(false, true);
	}
	
	private void do_run(boolean leaveDirty, boolean test){
		if(!test){
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			boolean cont = MessageDialog.openQuestion(shell, CDIUIMessages.QUESTION, CDIUIMessages.DECREASING_FIELD_VISIBILITY_MAY_CAUSE_COMPILATION_PROBLEMS);
			if(!cont)
				return;
		}
		try{
			if(cUnit == null) {
				return;
			}
			ICompilationUnit compilationUnit = cUnit.getWorkingCopy(new NullProgressMonitor());

			CompilationUnitChange change = getChange(compilationUnit);
			
			if(change.getEdit().hasChildren()){
				if(leaveDirty){
					change.setSaveMode(TextFileChange.LEAVE_DIRTY);
				}
				change.perform(new NullProgressMonitor());
				cUnit.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
			}
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

	private CompilationUnitChange getChange(ICompilationUnit compilationUnit) throws JavaModelException{
		CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
		
		MultiTextEdit edit = new MultiTextEdit();
		
		change.setEdit(edit);
		IBuffer buffer = compilationUnit.getBuffer();
		
		int flag = field.getFlags();
		
		String text = buffer.getText(field.getSourceRange().getOffset(), field.getSourceRange().getLength());

		int position = field.getSourceRange().getOffset();
		if(Flags.isPublic(flag)){
			position += text.indexOf(PUBLIC);
			TextEdit re = new ReplaceEdit(position, PUBLIC.length(), PROTECTED);
			edit.addChild(re);
		}
		
		return change;
	}
	
	private CompilationUnitChange getPreviewChange(){
		if(cUnit != null){
			try {
				ICompilationUnit compilationUnit = cUnit.getWorkingCopy(new NullProgressMonitor());
				
				CompilationUnitChange change = getChange(compilationUnit);
				
				compilationUnit.discardWorkingCopy();
				return change;
			} catch (JavaModelException e) {
				CommonPlugin.getDefault().logError(e);
			}
			
		}
		return null;
	}
	
	private String getPreview(){
		TextChange previewChange = getPreviewChange();
		
		try {
			return MarkerResolutionUtils.getPreview(previewChange);
		} catch (CoreException e) {
			CommonUIPlugin.getDefault().logError(e);
		}
		return label;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}

	@Override
	public int getRelevance() {
		return 100;
	}

	@Override
	public void apply(IDocument document) {
		do_run(true, false);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return description;
	}

	@Override
	public String getDisplayString() {
		return label;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}
}
