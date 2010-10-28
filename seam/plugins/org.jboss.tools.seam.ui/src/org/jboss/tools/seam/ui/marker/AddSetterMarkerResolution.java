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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
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
		IType type = (IType)javaDeclaration.getSourceMember();
		try{
			ICompilationUnit original = type.getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			IType createdType = compilationUnit.getType(type.getElementName());
			
			final String lineDelim= compilationUnit.findRecommendedLineSeparator();
			
			IField field = createdType.getField(property.getName());
			String propertyType="";
			if(field != null && field.exists()){
				propertyType = field.getTypeSignature();
			}else{
				propertyType = "String";
				field = createdType.createField(lineDelim+"private "+propertyType+" "+property.getName()+";", null, false, null);
//				synchronized(compilationUnit) {
//					compilationUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
//				}
			}
			String setterName = GetterSetterUtil.getSetterName(field, null);
			
			createMethod(createdType, propertyType, setterName, lineDelim);
			
			compilationUnit.commitWorkingCopy(true, new NullProgressMonitor());
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	private IMethod createMethod(IType type, String typeName, String methodName, String lineDelim) throws CoreException{
		StringBuffer buf= new StringBuffer();
		
		buf.append(lineDelim);
		buf.append("public void "+methodName+"("+typeName+" "+property.getName()+") {"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		buf.append(lineDelim);
		buf.append("this."+property.getName()+" = "+property.getName()+";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append(lineDelim);
		buf.append("}"); //$NON-NLS-1$
		return type.createMethod(buf.toString(), null, false, null);
	}

	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}

}
