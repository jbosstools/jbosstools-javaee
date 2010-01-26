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
package org.jboss.tools.cdi.ui.search;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.core.Member;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;

public class InjectionPointQueryParticipant implements IQueryParticipant{
	
	public int estimateTicks(QuerySpecification specification) {
		return 10;
	}

	public IMatchPresentation getUIParticipant() {
		return null;
	}

	public void search(ISearchRequestor requestor,
			QuerySpecification querySpecification, IProgressMonitor monitor)
			throws CoreException {
		
		if(querySpecification instanceof ElementQuerySpecification){
			if (!isSearchForReferences(querySpecification.getLimitTo()))
				return;
			
			ElementQuerySpecification qs = (ElementQuerySpecification)querySpecification;
			IJavaElement element = qs.getElement();
			if(element instanceof IMethod || element instanceof IField){
				IFile file = (IFile)element.getResource();
				
				CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
				
				if(cdiNature == null)
					return;
				
				ICDIProject cdiProject = cdiNature.getDelegate();
				
				if(cdiProject == null)
					return;
				
				Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
				
				if (element instanceof IAnnotatable) {
					IAnnotatable annotatable = (IAnnotatable)element;
					
					IAnnotation annotation = annotatable.getAnnotation("Injected");  //$NON-NLS-1$
					if (annotation == null)
						return;
					IInjectionPoint injectionPoint = CDIUtil.findInjectionPoint(beans, element);
					if(injectionPoint != null){
						Set<IBean> resultBeanSet = cdiProject.getBeans(injectionPoint);
						List<IBean> resultBeanList = CDIUtil.sortBeans(resultBeanSet);
						for(IBean bean : resultBeanList){
							if(bean != null){
								IType type = bean.getBeanClass();
								ISourceRange range = ((Member)type).getNameRange();
								Match match = new Match(type.getResource(), range.getOffset(), range.getLength());
								requestor.reportMatch(match);
							}
						}
					}
				}
			}
		}
	}
	
	public boolean isSearchForReferences(int limitTo) {
    	int maskedLimitTo = limitTo & ~(IJavaSearchConstants.IGNORE_DECLARING_TYPE+IJavaSearchConstants.IGNORE_RETURN_TYPE);
    	if (maskedLimitTo == IJavaSearchConstants.REFERENCES || maskedLimitTo == IJavaSearchConstants.ALL_OCCURRENCES) {
    		return true;
    	}
    
    	return false;
    }
	
}
