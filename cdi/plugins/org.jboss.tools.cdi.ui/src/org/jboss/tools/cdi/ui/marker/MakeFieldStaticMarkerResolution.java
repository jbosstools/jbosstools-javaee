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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.BaseMarkerResolution;

/**
 * @author Daniel Azarov
 */
public class MakeFieldStaticMarkerResolution extends BaseMarkerResolution {
	private IField field;
	private IFile file;
	
	public MakeFieldStaticMarkerResolution(IField field, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_FIELD_STATIC_MARKER_RESOLUTION_TITLE, new Object[]{field.getElementName()});
		this.field = field;
		this.file = file;
		init();
	}
	
	@Override
	protected ICompilationUnit getCompilationUnit(){
		return EclipseUtil.getCompilationUnit(file);
	}
	
	@Override
	protected CompilationUnitChange getChange(ICompilationUnit compilationUnit) {
		CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
		
		MultiTextEdit edit = new MultiTextEdit();
		
		change.setEdit(edit);
		try{
			IBuffer buffer = compilationUnit.getBuffer();
			
			int flag = field.getFlags();
			
			String text = buffer.getText(field.getSourceRange().getOffset(), field.getSourceRange().getLength());
	
			int position = field.getSourceRange().getOffset();
			if((flag & Flags.AccPublic) != 0){
				position += text.indexOf(CDIMarkerResolutionUtils.PUBLIC)+CDIMarkerResolutionUtils.PUBLIC.length();
				InsertEdit ie = new InsertEdit(position, CDIMarkerResolutionUtils.SPACE+CDIMarkerResolutionUtils.STATIC);
				edit.addChild(ie);
			}else if((flag & Flags.AccPrivate) != 0){
				position += text.indexOf(CDIMarkerResolutionUtils.PRIVATE)+CDIMarkerResolutionUtils.PRIVATE.length();
				InsertEdit ie = new InsertEdit(position, CDIMarkerResolutionUtils.SPACE+CDIMarkerResolutionUtils.STATIC);
				edit.addChild(ie);
			}else if((flag & Flags.AccProtected) != 0){
				position += text.indexOf(CDIMarkerResolutionUtils.PROTECTED)+CDIMarkerResolutionUtils.PROTECTED.length();
				InsertEdit ie = new InsertEdit(position, CDIMarkerResolutionUtils.SPACE+CDIMarkerResolutionUtils.STATIC);
				edit.addChild(ie);
			}else{
				String type = Signature.getSignatureSimpleName(field.getTypeSignature());
				position += text.indexOf(type);
				InsertEdit ie = new InsertEdit(position, CDIMarkerResolutionUtils.SPACE+CDIMarkerResolutionUtils.STATIC);
				edit.addChild(ie);
			}
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return change;
	}
	
	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}

}
