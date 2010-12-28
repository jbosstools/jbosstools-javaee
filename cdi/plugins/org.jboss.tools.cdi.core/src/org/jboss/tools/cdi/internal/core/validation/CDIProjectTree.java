/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.validation.IProjectValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree;

/**
 * Represents a tree of dependent CDI projects.
 * 
 * @author Alexey Kazakov
 */
public class CDIProjectTree implements IValidatingProjectTree {

	private Map<IProject, IValidatingProjectSet> brunches = new HashMap<IProject, IValidatingProjectSet>();
	private Set<IProject> validatingProjects = new HashSet<IProject>();
	private Set<IProject> allProjects = new HashSet<IProject>();

	/**
	 * @param project
	 */
	public CDIProjectTree(IProject project) {
		addProject(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree#addProject(org.eclipse.core.resources.IProject)
	 */
	public void addProject(IProject project) {
		if(project!=null) {
			allProjects.add(project);
			if(!validatingProjects.contains(project)) {
				validatingProjects.add(project);
				CDICoreNature currentNature = CDICorePlugin.getCDI(project, false);
				if(currentNature!=null) {
					Set<CDICoreNature> roots = getRootProjects(currentNature);
					for (CDICoreNature root : roots) {
						IProject rootProject = root.getProject();
						if(rootProject!=null && rootProject.isAccessible()) {
							Set<IProject> requiredProjects = collectRequiredProjects(root);
							IKbProject kbProject = KbProjectFactory.getKbProject(rootProject, false);
							IProjectValidationContext rootContext;
							if(kbProject!=null) {
								rootContext = kbProject.getValidationContext();
							} else {
								KbProject.checkKBBuilderInstalled(rootProject);
								rootContext = root.getValidationContext();
							}
							requiredProjects.add(root.getProject());
							IValidatingProjectSet brunch = new ValidatingProjectSet(root.getProject(), requiredProjects, rootContext);
							brunches.put(rootProject, brunch);
							allProjects.addAll(brunch.getAllProjects());
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree#getBrunches()
	 */
	public Map<IProject, IValidatingProjectSet> getBrunches() {
		return brunches;
	}

	/**
	 * @return the validatingProjects
	 */
	public Set<IProject> getValidatingProjects() {
		return validatingProjects;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree#getAllProjects()
	 */
	public Set<IProject> getAllProjects() {
		return allProjects;
	}

	private Set<CDICoreNature> getRootProjects(CDICoreNature project) {
		Set<CDICoreNature> result = new HashSet<CDICoreNature>();
		Set<CDICoreNature> dependentProjects = project.getDependentProjects();
		if(dependentProjects.isEmpty()) {
			result.add(project);
		} else {
			for (CDICoreNature nature : dependentProjects) {
				if(!nature.getProject().isAccessible()) continue;
				if(!result.contains(nature)) {
					result.addAll(getRootProjects(nature));
				}
			}
		}
		return result;
	}

	private Set<IProject> collectRequiredProjects(CDICoreNature project) {
		return collectRequiredProjects(new HashSet<IProject>(), project);
	}

	private Set<IProject> collectRequiredProjects(Set<IProject> dependsOn, CDICoreNature project) {
		Set<CDICoreNature> cdiProjects = project.getCDIProjects();
		for (CDICoreNature cdiCoreNature : cdiProjects) {
			IProject includedProject = cdiCoreNature.getProject();
			if(!dependsOn.contains(includedProject)) {
				dependsOn.add(includedProject);
				collectRequiredProjects(dependsOn, cdiCoreNature);
			}
		}
		return dependsOn;
	}
}