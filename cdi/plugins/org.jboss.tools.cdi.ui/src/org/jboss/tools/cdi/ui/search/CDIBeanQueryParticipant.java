/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.util.BeanPresentationUtil;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.java.IParametedType;

public class CDIBeanQueryParticipant implements IQueryParticipant{
	static CDIBeanLabelProvider labelProvider = new CDIBeanLabelProvider();
	
	@Override
	public void search(ISearchRequestor requestor,
			QuerySpecification querySpecification, IProgressMonitor monitor)
			throws CoreException {
		if(querySpecification instanceof ElementQuerySpecification){
			if (!isSearchForReferences(querySpecification.getLimitTo())) {
				return;
			}
			
			ElementQuerySpecification qs = (ElementQuerySpecification)querySpecification;
			IJavaElement element = qs.getElement();
			IProject project = element.getJavaProject().getProject();
			
			ICDIProject cdiProject = CDICorePlugin.getCDIProject(project, true);
			
			if(cdiProject == null) {
				return;
			}
			
			searchInProject(requestor, querySpecification, cdiProject, monitor, element);
			
			CDICoreNature[] natures = cdiProject.getNature().getAllDependentProjects(true);
			for(CDICoreNature nature : natures){
				ICDIProject p = nature.getDelegate();
				if(p != null){
					searchInProject(requestor, querySpecification, p, monitor, element);
				}
			}
			
		}
	}
	
	private void searchInProject(ISearchRequestor requestor, QuerySpecification querySpecification, ICDIProject cdiProject, IProgressMonitor monitor, IJavaElement element){
		Set<IBean> sourceBeans = cdiProject.getBeans(element);
		
		Set<IInjectionPoint> injectionPoints = new HashSet<IInjectionPoint>();
		for (IBean b: sourceBeans) {
			Set<IParametedType> ts = b.getLegalTypes();
			for (IParametedType t: ts) {
				injectionPoints.addAll(cdiProject.getInjections(t.getType().getFullyQualifiedName()));
			}
		}
		
		monitor.beginTask(CDIUIMessages.CDI_BEAN_QUERY_PARTICIPANT_TASK, injectionPoints.size());
			
		for(IInjectionPoint injectionPoint : injectionPoints){
			if(monitor.isCanceled())
				break;
			Set<IBean> resultBeans = cdiProject.getBeans(false, injectionPoint);
			monitor.worked(1);
				
			for(IBean cBean : resultBeans){
				if(sourceBeans.contains(cBean) && InjectionPointQueryParticipant.containsInSearchScope(querySpecification, cBean)){
					Match match = new CDIMatch(injectionPoint);
					requestor.reportMatch(match);
					break;
				}
			}
		}
		monitor.done();
		
	}
	
	public boolean isSearchForReferences(int limitTo) {
    	int maskedLimitTo = limitTo & ~(IJavaSearchConstants.IGNORE_DECLARING_TYPE+IJavaSearchConstants.IGNORE_RETURN_TYPE);
    	if (maskedLimitTo == IJavaSearchConstants.REFERENCES || maskedLimitTo == IJavaSearchConstants.ALL_OCCURRENCES) {
    		return true;
    	}
    
    	return false;
    }

	@Override
	public int estimateTicks(QuerySpecification specification) {
		return 10;
	}

	@Override
	public IMatchPresentation getUIParticipant() {
		return new CDIBeanMatchPresentation();
	}
	
	class CDIBeanMatchPresentation implements IMatchPresentation{

		@Override
		public ILabelProvider createLabelProvider() {
			return labelProvider;
		}

		@Override
		public void showMatch(Match match, int currentOffset,
				int currentLength, boolean activate) throws PartInitException {
			if(match instanceof CDIMatch){
				IJavaElement element = ((CDIMatch)match).getJavaElement();
				if(element != null){
					try{
						JavaUI.openInEditor(element);
					}catch(JavaModelException ex){
						CDIUIPlugin.getDefault().logError(ex);
					}catch(PartInitException ex){
						CDIUIPlugin.getDefault().logError(ex);
					}
				}
			}

		}
		
	}
	
	static class CDIBeanLabelProvider implements ILabelProvider{

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			if(element instanceof CDIElementWrapper){
				return CDIImages.getImageByElement(((CDIElementWrapper)element).getCDIElement());
			}
			return CDIImages.WELD_IMAGE;
		}

		@Override
		public String getText(Object element) {
			if(element instanceof CDIElementWrapper){
				ICDIElement cdiElement = ((CDIElementWrapper)element).getCDIElement();
				String kind = BeanPresentationUtil.getCDIElementKind(cdiElement);
				String text = "";
				if(kind != null){
					text = kind+" ";
				}
				return text+cdiElement.getElementName()+BeanPresentationUtil.getCDIElementLocation(cdiElement, false);
			}
			return ""; //$NON-NLS-1$

		}
		
	}

}
