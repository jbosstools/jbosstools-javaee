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
package org.jboss.tools.seam.ui.marker;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * @author Daniel Azarov
 */
public class AddSetterMarkerResolution implements IMarkerResolution2{
	private ISeamProperty property;
	private ISeamJavaComponentDeclaration javaDeclaration;
	
	private String label;
	
	public AddSetterMarkerResolution(ISeamProperty property, ISeamJavaComponentDeclaration javaDeclaration){
		this.property = property;
		this.javaDeclaration = javaDeclaration;
		this.label = MessageFormat.format(SeamUIMessages.ADD_SETTER_MARKER_RESOLUTION_TITLE, new Object[]{property.getName(), javaDeclaration.getClassName()});
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		IFile file = (IFile)javaDeclaration.getResource();
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			String lineDelim= compilationUnit.findRecommendedLineSeparator();
			
			IType type = compilationUnit.findPrimaryType();
			
			IField field = type.getField(property.getName());
			
			String propertyType="";
			if(field != null && field.exists()){
				propertyType = field.getTypeSignature();
			}else{
				propertyType = "String"; //$NON-NLS-1$
				
				StringBuffer buf= new StringBuffer();
				
				buf.append("private "+propertyType+" "+property.getName()+";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				buf.append(lineDelim);
				
				field = type.createField(buf.toString(), null, false, new NullProgressMonitor());
			}
			
			String setterName = GetterSetterUtil.getSetterName(field, null);
			IMethod oldMethod = GetterSetterUtil.getSetter(field);
			if(oldMethod == null || !oldMethod.exists()){
				String stub = GetterSetterUtil.getSetterStub(field, setterName, true, Flags.AccPublic);
				type.createMethod(stub, null, false, new NullProgressMonitor());
			}
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();

		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}

}
