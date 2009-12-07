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
package org.jboss.tools.cdi.internal.core.scanner.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.AbstractClassPathMonitor;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.web.kb.internal.scanner.LibraryScanner;
import org.jboss.tools.jst.web.kb.internal.scanner.LoadedDeclarations;
import org.jboss.tools.jst.web.kb.internal.scanner.ScannerException;

public class ClassPathMonitor extends AbstractClassPathMonitor<CDICoreNature>{

	public ClassPathMonitor(CDICoreNature project) {
		this.project = project;
	}

	public void init() {
		model = EclipseResourceUtil.createObjectForResource(getProjectResource()).getModel();
	}

	public void validateProjectDependencies() {
		//TODO
	}

	public List<String> process() {
		List<String> newJars = new ArrayList<String>();
		Iterator<String> it = processedPaths.iterator();
		while(it.hasNext()) {
			String p = it.next();
			if(paths.contains(p)) continue;
			project.pathRemoved(new Path(p));
			it.remove();
		}
		for (int i = 0; i < paths.size(); i++) {
			String p = paths.get(i);
			if(processedPaths.contains(p)) continue;
			processedPaths.add(p);

			String fileName = new File(p).getName();
			if(EclipseResourceUtil.SYSTEM_JAR_SET.contains(fileName)) continue;
			String jsname = "lib-" + fileName; //$NON-NLS-1$
			XModelObject o = model.getByPath("FileSystems").getChildByPath(jsname); //$NON-NLS-1$
			if(o == null) continue;
			XModelObject b = o.getChildByPath("META-INF/beans.xml");
			if(b == null) continue;
			newJars.add(p);
		}
		
		return newJars;
//		validateProjectDependencies();
	}

	public boolean hasToUpdateProjectDependencies() {
		//TODO
		return false;
	}

	public IProject getProjectResource() {
		return project.getProject();
	}

}
