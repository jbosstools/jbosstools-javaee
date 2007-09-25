/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * Helper class that collects related J2EE projects for 
 * a given 'seed' project.
 * 
 * If seed project is EAR, it's referenced projects are used to fill 
 * lists with WAR and EJB projects.
 * 
 * If seed project is referenced by a EAR project (the first occurrence is taken),
 * that EAR is used as seed project.
 *  
 * If seed project is WAR or EJB not referenced by any EAR project,
 * field 'ear' remains null, and only lists 'wars' and 'ejbs' are available.
 * 
 * Also this class provides helper methods to obtain root folders 
 * for involved EAR, WAR and EJB projects.
 * 
 * @author Viacheslav Kabanovich
 */
public class J2EEProjects {
	IProject ear;
	List<IProject> wars = new ArrayList<IProject>();
	List<IProject> ejbs = new ArrayList<IProject>();

	/**
	 * Returns instance of J2EEProjects  
	 * if parameter project is a J2EE project,
	 * otherwise null is returned.
	 * If parameter project is EAR, referenced projects
	 * are used to fill lists 'wars' and ears'
	 * If parameter project is WAR or EJB and has referencing EAR,
	 * then that EAR project is considered as current project, 
	 * otherwise referenced EJB projects are put to 'ears' list.
	 * @param project
	 * @return
	 */
	public static J2EEProjects create(IProject project) {
		boolean isWar = J2EEProjectUtilities.isDynamicWebProject(project);
		boolean isEar = J2EEProjectUtilities.isEARProject(project);
		boolean isEJB = J2EEProjectUtilities.isEJBProject(project);
		if(!isEar && !isEJB && !isWar) return null;
		return new J2EEProjects(project);
	}

	private J2EEProjects(IProject project) {
		if(J2EEProjectUtilities.isDynamicWebProject(project)) {
			wars.add(project);
		} else if(J2EEProjectUtilities.isEARProject(project)) {
			ear = project;
		} else if(J2EEProjectUtilities.isEJBProject(project)) {
			ejbs.add(project);
		}
		if(ear == null) {
			IProject[] ps = J2EEProjectUtilities.getReferencingEARProjects(project);
			if(ps != null && ps.length > 0) ear = ps[0];
		}
		if(ear != null || wars.size() > 0) {
			IProject seed = (ear != null) ? ear : project;
			IVirtualComponent component = ComponentCore.createComponent(seed);
			IVirtualReference[] rs = component.getReferences();
			for (int i = 0; i < rs.length; i++) {
				IVirtualComponent c = rs[i].getReferencedComponent();
				if(c == null) continue;
				IProject p = c.getRootFolder().getProject();
				if(J2EEProjectUtilities.isDynamicWebProject(p)) {
					if(!wars.contains(p)) wars.add(p);
				} else if(J2EEProjectUtilities.isEJBProject(project)) {
					if(!ejbs.contains(p)) ejbs.add(p);
				}
			}
		}
	}
	
	/**
	 * Returns EAR project or null, if WAR project is not used by EAR.
	 * @return
	 */
	public IProject getEARProject() {
		return ear;
	}

	/**
	 * Returns Content folder of EAR project or null 
	 * if EAR is not available.
	 * @return
	 */	
	public IFolder getEARContentFolder() {
		if(ear == null) return null;
		IVirtualComponent component = ComponentCore.createComponent(ear);
		IPath path = component.getRootFolder().getProjectRelativePath();
		return path == null ? null : ear.getFolder(path);
	}
	
	/**
	 * Returns list of WAR projects.
	 * @return
	 */
	public List<IProject> getWARProjects() {
		return wars;
	}

	/**
	 * Returns Content folder for first found WAR project. 
	 * @return
	 */
	public IFolder getWARContentFolder() {
		if(wars.size() == 0) return null;
		IVirtualComponent component = ComponentCore.createComponent(wars.get(0));
		IPath path = component.getRootFolder().getProjectRelativePath();
		return path == null ? null : wars.get(0).getFolder(path);
	}
	
	/**
	 * Returns list of EJB projects.
	 * @return
	 */
	public List<IProject> getEJBProjects() {
		return ejbs;
	}

	/**
	 * Returns source roots for first found EJB project.
	 * @return
	 */
	public IResource[] getEJBSourceRoots() {
		return ejbs.size() == 0 ? new IResource[0] : EclipseResourceUtil.getJavaSourceRoots(ejbs.get(0));
	}
	
}
