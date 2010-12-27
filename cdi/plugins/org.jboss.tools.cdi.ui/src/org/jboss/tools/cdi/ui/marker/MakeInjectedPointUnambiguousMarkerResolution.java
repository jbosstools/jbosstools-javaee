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
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.internal.Workbench;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanComposite;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizard;
import org.jboss.tools.common.EclipseUtil;

/**
 * @author Daniel Azarov
 */
public class MakeInjectedPointUnambiguousMarkerResolution implements IMarkerResolution2 {
	private String label;
	private IFile file;
	private IInjectionPoint injectionPoint;
	private List<IBean> beans;
	private IBean selectedBean;
	
	public MakeInjectedPointUnambiguousMarkerResolution(IInjectionPoint injectionPoint, List<IBean> beans, IFile file, int index){
		this.file = file;
		this.injectionPoint = injectionPoint;
		this.beans = beans;
		this.selectedBean = beans.get(index);
		this.label = MessageFormat.format(CDIUIMessages.MAKE_INJECTED_POINT_UNAMBIGUOUS_TITLE, new Object[]{selectedBean.getBeanClass().getElementName()});
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		if(checkBeans()){
			Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
			AddQualifiersToBeanWizard wizard = new AddQualifiersToBeanWizard(injectionPoint, beans, selectedBean);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			int status = dialog.open();
			if(status != WizardDialog.OK)
				return;
			
			List<IQualifier> deployed = wizard.getDeployedQualifiers();
			addQualifiersToBean(deployed);
			try {
				Thread.sleep(3000);
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		addQualifiersToInjectedPoint();
	}
	
	private void addQualifiersToBean(List<IQualifier> deployed){
		IFile file = (IFile)selectedBean.getBeanClass().getResource();
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IType type = compilationUnit.findPrimaryType();
			if(type != null){
				for(IQualifier qualifier : deployed){
					MarkerResolutionUtils.addAnnotation(qualifier.getSourceType().getFullyQualifiedName(), compilationUnit, type);
				}
			}
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

	private void addQualifiersToInjectedPoint(){
		try{
			ICompilationUnit original = injectionPoint.getClassBean().getBeanClass().getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			Set<IQualifier> qualifiers = selectedBean.getQualifiers();
			for(IQualifier qualifier : qualifiers){
				String qualifierName = qualifier.getSourceType().getFullyQualifiedName();
				if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) && !qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
					IJavaElement element = getInjectedJavaElement(compilationUnit);
					MarkerResolutionUtils.addQualifier(qualifierName, compilationUnit, element);
				}
			}

			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}
	
	private IJavaElement getInjectedJavaElement(ICompilationUnit compolationUnit){
		if(injectionPoint instanceof IInjectionPointField){
			IField field = ((IInjectionPointField)injectionPoint).getField();
			IType type = field.getDeclaringType();
			IType t = compolationUnit.getType(type.getElementName());
			IField f = t.getField(field.getElementName());
			
			return f;
		}else if(injectionPoint instanceof IInjectionPointMethod){
			IMethod method = ((IInjectionPointMethod)injectionPoint).getMethod();
			IType type = method.getDeclaringType();
			IType t = compolationUnit.getType(type.getElementName());
			IMethod m = t.getMethod(method.getElementName(), method.getParameterTypes());
			
			return m;
		}else if(injectionPoint instanceof IInjectionPointParameter){
			String paramName = ((IInjectionPointParameter)injectionPoint).getName();
			IMethod method =  ((IInjectionPointParameter)injectionPoint).getBeanMethod().getMethod();
			IType type = method.getDeclaringType();
			IType t = compolationUnit.getType(type.getElementName());
			IMethod m = t.getMethod(method.getElementName(), method.getParameterTypes());
			ITypeParameter p = m.getTypeParameter(paramName);
			
			return p;
		}
		return null;
	}
	
	private boolean checkBeans(){
		Set<IQualifier> qualifiers = selectedBean.getQualifiers();
		if(qualifiers.size() == 0)
			return true;
		
		for(IBean bean: beans){
			if(bean.equals(selectedBean))
				continue;
			if(AddQualifiersToBeanComposite.checkBeanQualifiers(bean, qualifiers))
				return true;
				
		}
		return false;
	}
	
	
	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}

}
