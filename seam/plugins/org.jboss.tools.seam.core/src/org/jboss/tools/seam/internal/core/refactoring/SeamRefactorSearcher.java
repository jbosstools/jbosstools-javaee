/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.refactoring.RefactorSearcher;
import org.jboss.tools.common.el.core.resolver.ELCompletionEngine;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELResolverFactoryManager;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.SimpleELContext;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.SeamProjectsSet;

public abstract class SeamRefactorSearcher extends RefactorSearcher {
	private SeamProjectsSet projectsSet;
	private ISeamComponent component;
	
	public SeamRefactorSearcher(IFile file, String name){
		super(file, name);
		projectsSet = new SeamProjectsSet(file.getProject());
	}
	
	public SeamRefactorSearcher(IFile file, String name, IJavaElement element){
		this(file, name);
		javaElement = element;
	}
	
	public SeamRefactorSearcher(IFile file, String name, ISeamComponent component){
		this(file, name);
		this.component = component;
	}
	
	protected IProject[] getProjects(){
		return projectsSet.getAllProjects();
	}
	
	protected IContainer getViewFolder(IProject project){
		if(project.equals(projectsSet.getWarProject()))
			return projectsSet.getDefaultViewsFolder();
		else if(project.equals(projectsSet.getEarProject()))
			return projectsSet.getDefaultEarViewsFolder();
		
		return null;
	}
	
	@Override
	protected boolean isFileCorrect(IFile file){
		if(!file.isSynchronized(IResource.DEPTH_ZERO)){
			return false;
		}else if(file.isPhantom()){
			return false;
		}else if(file.isReadOnly()){
			return false;
		}
		return true;
	}
	
	protected void checkMatch(IFile file, ELExpression operand, int offset, int length){
		if(javaElement != null && operand != null)
			resolve(file, operand, offset-getOffset((ELInvocationExpression)operand));
		else if(component != null && operand != null)
			resolveComponentsReferences(file, operand, offset-getOffset((ELInvocationExpression)operand));
		else
			match(file, offset, length);
	}

	
	private void resolveComponentsReferences(IFile file, ELExpression operand, int offset) {
		ELResolver[] resolvers = ELResolverFactoryManager.getInstance()
				.getResolvers(file);

		for (ELResolver resolver : resolvers) {
			if (!(resolver instanceof ELCompletionEngine))
				continue;

			SimpleELContext context = new SimpleELContext();

			context.setResource(file);
			context.setElResolvers(resolvers);

			List<Var> vars = ElVarSearcher.findAllVars(context, offset,
					resolver);

			context.setVars(vars);

			ELResolution resolution = resolver.resolve(context, operand);

			List<ELSegment> segments = resolution.findSegmentsByVariable(component);
			
			for(ELSegment segment : segments){
				match(file, offset+segment.getSourceReference().getStartPosition(), segment.getSourceReference().getLength());
			}
		}
	}

}
