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
package org.jboss.tools.cdi.ui.marker;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
import org.jboss.tools.common.ui.CommonUIPlugin;

/**
 * @author Daniel Azarov
 */
public class AddLocalBeanMarkerResolution implements IMarkerResolution2 {
	private static final String PUBLIC = "public";  //$NON-NLS-1$
	private static final String PRIVATE = "private";  //$NON-NLS-1$
	private static final String PROTECTED = "protected";  //$NON-NLS-1$
	private static final String SPACE = " ";  //$NON-NLS-1$
	
	private String label;
	private String description;
	private IMethod method;
	private IFile file;
	
	public AddLocalBeanMarkerResolution(IMethod method, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.ADD_LOCAL_BEAN_MARKER_RESOLUTION_TITLE, new Object[]{method.getDeclaringType().getElementName()});
		this.method = method;
		this.file = file;
		description = getPreview();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			if(original == null) {
				return;
			}
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			CompilationUnitChange change = getChange(compilationUnit);
			
			if(change.getEdit().hasChildren()){
				change.perform(new NullProgressMonitor());
				original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
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
		
		int flag = method.getFlags();
		
		String text = buffer.getText(method.getSourceRange().getOffset(), method.getSourceRange().getLength());

		// make method public
		int position = method.getSourceRange().getOffset();
		
		if((flag & Flags.AccPublic) == 0){
			if((flag & Flags.AccPrivate) != 0){
				position += text.indexOf(PRIVATE);
				edit.addChild(new ReplaceEdit(position, PRIVATE.length(), PUBLIC));
			}else if((flag & Flags.AccProtected) != 0){
				position += text.indexOf(PROTECTED);
				edit.addChild(new ReplaceEdit(position, PROTECTED.length(), PUBLIC));
			}else{
				String type = Signature.getSignatureSimpleName(method.getReturnType());
				position += text.indexOf(type);
				buffer.replace(position, 0, PUBLIC+SPACE);
				edit.addChild(new InsertEdit(position, PUBLIC+SPACE));
			}
		}
		
		// add @LocalBean annotation
		MarkerResolutionUtils.addAnnotation(CDIConstants.LOCAL_BEAN_ANNOTATION_TYPE_NAME, compilationUnit, method.getDeclaringType(), "", edit);
		
		return change;
	}
	
	private CompilationUnitChange getPreviewChange(){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			
			return getChange(original);
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
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
		return CDIImages.QUICKFIX_ADD;
	}

}
