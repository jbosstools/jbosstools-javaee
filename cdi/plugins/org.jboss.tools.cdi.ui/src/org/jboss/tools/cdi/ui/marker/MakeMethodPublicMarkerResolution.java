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
import org.eclipse.jdt.core.Signature;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;

/**
 * @author Daniel Azarov
 */
public class MakeMethodPublicMarkerResolution implements IMarkerResolution2 {
	private static final String PUBLIC = "public";  //$NON-NLS-1$
	private static final String PRIVATE = "private";  //$NON-NLS-1$
	private static final String PROTECTED = "protected";  //$NON-NLS-1$
	private static final String SPACE = " ";  //$NON-NLS-1$
	
	private String label;
	private IMethod method;
	private IFile file;
	
	public MakeMethodPublicMarkerResolution(IMethod method, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_METHOD_PUBLIC_MARKER_RESOLUTION_TITLE, new Object[]{method.getElementName()});
		this.method = method;
		this.file = file;
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IBuffer buffer = compilationUnit.getBuffer();
			
			int flag = method.getFlags();
			
			String text = buffer.getText(method.getSourceRange().getOffset(), method.getSourceRange().getLength());

			// make method public
			int position = method.getSourceRange().getOffset();
			if((flag & Flags.AccPublic) != 0){
				// do nothing
			}else if((flag & Flags.AccPrivate) != 0){
				position += text.indexOf(PRIVATE);
				buffer.replace(position, PRIVATE.length(), PUBLIC);
			}else if((flag & Flags.AccProtected) != 0){
				position += text.indexOf(PROTECTED);
				buffer.replace(position, PROTECTED.length(), PUBLIC);
			}else{
				String type = Signature.getSignatureSimpleName(method.getReturnType());
				position += text.indexOf(type);
				buffer.replace(position, 0, PUBLIC+SPACE);
			}
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}

}
