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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatingProjectSet;

/**
 * @author Alexey Kazakov
 */
public class CDIProjectSet extends ValidatingProjectSet {

	private ICDIProject rootCdiProject;

	/**
	 * @param project
	 */
	public CDIProjectSet(IProject project) {
		allProjects = new HashSet<IProject>();
		allProjects.add(project);
		CDICoreNature sp = CDICorePlugin.getCDI(project, false);
		if(sp!=null) {
			addIncludedProjects(sp);
			CDICoreNature rootCdiNature = addIncludingProjects(sp);
			rootCdiProject = rootCdiNature.getDelegate();
			rootProject = rootCdiNature.getProject();

			if(rootProject!=null && rootProject.isAccessible()) {
				IKbProject kbProject = KbProjectFactory.getKbProject(rootProject, false);
				if(kbProject!=null) {
					rootContext = kbProject.getValidationContext();
				} else {
					KbProject.checkKBBuilderInstalled(rootProject);
					rootContext = rootCdiNature.getValidationContext();
				}
			}
		}
	}

	private CDICoreNature addIncludingProjects(CDICoreNature project) {
		Set<CDICoreNature> dependentProjects = project.getDependentProjects();
		for (CDICoreNature nature : dependentProjects) {
			if(allProjects.contains(nature.getProject())) {
				return project;
			}
			if(!nature.getProject().isAccessible()) continue;
			allProjects.add(nature.getProject());
			return addIncludingProjects(nature);
		}
		return project;
	}

	private void addIncludedProjects(CDICoreNature project) {
		Set<CDICoreNature> includedCdiProjects = project.getCDIProjects();
		for (CDICoreNature cdiCoreNature : includedCdiProjects) {
			IProject includedProject = cdiCoreNature.getProject();
			if(!allProjects.contains(includedProject)) {
				allProjects.add(includedProject);
				addIncludedProjects(cdiCoreNature);
			}
		}
	}

	public void setRootCdiProject(ICDIProject rootCdiProject) {
		this.rootCdiProject = rootCdiProject;
	}

	public ICDIProject getRootCdiProject() {
		return rootCdiProject;
	}
}