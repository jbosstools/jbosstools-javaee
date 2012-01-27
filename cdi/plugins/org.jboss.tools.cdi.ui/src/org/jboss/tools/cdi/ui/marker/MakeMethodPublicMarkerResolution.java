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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.BaseMarkerResolution;

/**
 * @author Daniel Azarov
 */
public class MakeMethodPublicMarkerResolution extends BaseMarkerResolution {
	private IMethod method;
	private IFile file;
	
	public MakeMethodPublicMarkerResolution(IMethod method, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_METHOD_PUBLIC_MARKER_RESOLUTION_TITLE, new Object[]{method.getElementName()});
		this.method = method;
		this.file = file;
		init();
	}

	@Override
	protected ICompilationUnit getCompilationUnit(){
		return EclipseUtil.getCompilationUnit(file);
	}

	@Override
	protected CompilationUnitChange getChange(ICompilationUnit compilationUnit){
		CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
		
		MultiTextEdit edit = new MultiTextEdit();
		
		change.setEdit(edit);
		
		try{
			IBuffer buffer = compilationUnit.getBuffer();
			
			IMethod workingCopyMethod = CDIMarkerResolutionUtils.findWorkingCopy(compilationUnit, method);
			if(workingCopyMethod != null){
				int flag = workingCopyMethod.getFlags();
				
				String text = buffer.getText(workingCopyMethod.getSourceRange().getOffset(), workingCopyMethod.getSourceRange().getLength());
	
				// make method public
				int position = workingCopyMethod.getSourceRange().getOffset();
				if(!Flags.isPublic(flag)){
					if(Flags.isPrivate(flag)){
						position += text.indexOf(CDIMarkerResolutionUtils.PRIVATE);
						ReplaceEdit re = new ReplaceEdit(position, CDIMarkerResolutionUtils.PRIVATE.length(), CDIMarkerResolutionUtils.PUBLIC);
						edit.addChild(re);
					}else if(Flags.isProtected(flag)){
						position += text.indexOf(CDIMarkerResolutionUtils.PROTECTED);
						ReplaceEdit re = new ReplaceEdit(position, CDIMarkerResolutionUtils.PROTECTED.length(), CDIMarkerResolutionUtils.PUBLIC);
						edit.addChild(re);
					}else{
						String type = Signature.getSignatureSimpleName(workingCopyMethod.getReturnType());
						position += text.indexOf(type);
						InsertEdit ie = new InsertEdit(position, CDIMarkerResolutionUtils.PUBLIC+CDIMarkerResolutionUtils.SPACE);
						edit.addChild(ie);
					}
				}
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
