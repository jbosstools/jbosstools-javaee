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
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;

/**
 * @author Daniel Azarov
 */
public class MakeFieldStaticMarkerResolution implements IMarkerResolution2 {
	private static final String PUBLIC = "public";  //$NON-NLS-1$
	private static final String PRIVATE = "private";  //$NON-NLS-1$
	private static final String PROTECTED = "protected";  //$NON-NLS-1$
	private static final String STATIC = "static";  //$NON-NLS-1$
	private static final String SPACE = " ";  //$NON-NLS-1$
	
	private String label;
	private IField field;
	private IFile file;
	
	public MakeFieldStaticMarkerResolution(IField field, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_FIELD_STATIC_MARKER_RESOLUTION_TITLE, new Object[]{field.getElementName()});
		this.field = field;
		this.file = file;
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

			CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			change.setEdit(edit);
			IBuffer buffer = compilationUnit.getBuffer();
			
			int flag = field.getFlags();
			
			String text = buffer.getText(field.getSourceRange().getOffset(), field.getSourceRange().getLength());

			int position = field.getSourceRange().getOffset();
			if((flag & Flags.AccPublic) != 0){
				position += text.indexOf(PUBLIC)+PUBLIC.length();
				InsertEdit ie = new InsertEdit(position, SPACE+STATIC);
				edit.addChild(ie);
				//buffer.replace(position, 0, SPACE+STATIC);
			}else if((flag & Flags.AccPrivate) != 0){
				position += text.indexOf(PRIVATE)+PRIVATE.length();
				InsertEdit ie = new InsertEdit(position, SPACE+STATIC);
				edit.addChild(ie);
				//buffer.replace(position, 0, SPACE+STATIC);
			}else if((flag & Flags.AccProtected) != 0){
				position += text.indexOf(PROTECTED)+PROTECTED.length();
				InsertEdit ie = new InsertEdit(position, SPACE+STATIC);
				edit.addChild(ie);
				//buffer.replace(position, 0, SPACE+STATIC);
			}else{
				String type = Signature.getSignatureSimpleName(field.getTypeSignature());
				position += text.indexOf(type);
				InsertEdit ie = new InsertEdit(position, SPACE+STATIC);
				edit.addChild(ie);
				//buffer.replace(position, 0, STATIC+SPACE);
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
