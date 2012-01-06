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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * @author Daniel Azarov
 */
public class MakeMethodBusinessMarkerResolution implements IMarkerResolution2 {
	private static final String PUBLIC = "public";  //$NON-NLS-1$
	private static final String PRIVATE = "private";  //$NON-NLS-1$
	private static final String PROTECTED = "protected";  //$NON-NLS-1$
	private static final String SPACE = " ";  //$NON-NLS-1$
	
	
	private String label;
	private IMethod method;
	private IType localInterface;
	private IFile file;
	
	public MakeMethodBusinessMarkerResolution(IMethod method, IType localInterface, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_METHOD_BUSINESS_MARKER_RESOLUTION_TITLE, new Object[]{method.getElementName(), localInterface.getElementName()});
		this.method = method;
		this.localInterface = localInterface;
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
			
			// add method to interface
			
			original = localInterface.getCompilationUnit();
			compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IType interfaceType = compilationUnit.getType(localInterface.getElementName());
			
			StringBuffer content = new StringBuffer();
			
			content.append(PUBLIC+SPACE);
			
			String simpleType = Signature.getSignatureSimpleName(method.getReturnType()); 
			content.append(simpleType);
			content.append(SPACE);
			content.append(method.getElementName());
			content.append("("); //$NON-NLS-1$
			
			IType originalType = method.getDeclaringType();
			
			addImport(originalType, simpleType, compilationUnit);
			
			String[] types = method.getParameterTypes();
			String[] names = method.getParameterNames();
			
			for(int i = 0; i < method.getNumberOfParameters(); i++){
				if(i > 0)
					content.append(", "); //$NON-NLS-1$
				
				simpleType = Signature.getSignatureSimpleName(types[i]);
				
				addImport(originalType, simpleType, compilationUnit);
				
				content.append(simpleType);
				content.append(SPACE);
				content.append(names[i]);
			}
			
			content.append(");"); //$NON-NLS-1$
			
			interfaceType.createMethod(content.toString(), null, false, new NullProgressMonitor());
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}
	
	private void addImport(IType originalType, String simpleName, ICompilationUnit compilationUnit) throws JavaModelException{
		String qualifiedName = EclipseJavaUtil.resolveType(originalType, simpleName);
		CDIMarkerResolutionUtils.addImport(qualifiedName, compilationUnit);
	}
	

	@Override
	public String getDescription() {
		return label;
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_ADD;
	}

}
