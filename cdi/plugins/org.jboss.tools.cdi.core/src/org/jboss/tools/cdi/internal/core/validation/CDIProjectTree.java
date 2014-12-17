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
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectSet;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.internal.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;

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
				CDICoreNature currentNature = CDICorePlugin.getCDI(project, true); //because we do not store it
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
							
							for (IProject p: requiredProjects) {
								CDICorePlugin.getCDI(p, true); //all should be active.
							}
							
							IValidatingProjectSet brunch = new ValidatingProjectSet(root.getProject(), requiredProjects, rootContext){
								@Override
								public boolean isFullValidationRequired() {
									for (IProject p: getAllProjects()) {
										CDICoreNature n = CDICorePlugin.getCDI(p, false);
										if(n != null && n.getValidationContext().isFullValidationRequired()) {
											return true;
										}
									}
									return false;
								}

								@Override
								public void setFullValidationRequired(boolean b) {
									for (IProject p: getAllProjects()) {
										CDICoreNature n = CDICorePlugin.getCDI(p, false);
										if(n != null && n.getValidationContext().isFullValidationRequired()) {
											n.getValidationContext().setFullValidationRequired(false);
										}
									}
								}
							};
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
		CDICoreNature[] dependentProjects = project.getAllDependentProjects(true);
		if(dependentProjects.length == 0) {
			result.add(project);
		} else {
			for (CDICoreNature nature : dependentProjects) {
				if(!nature.getProject().isAccessible()) continue;
				if(nature.getDependentProjects().isEmpty()) {
					result.add(nature);
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