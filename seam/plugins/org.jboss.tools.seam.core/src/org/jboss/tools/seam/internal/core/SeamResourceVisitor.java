/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamResourceVisitor implements IResourceVisitor {
	static IFileScanner[] FILE_SCANNERS = {
		new JavaScanner(), 
		new XMLScanner(), 
	};
	SeamProject p;
	
	public SeamResourceVisitor(SeamProject p) {
		this.p = p;
	}
	
	public IResourceVisitor getVisitor() {
		return this;
	}

	public boolean visit(IResource resource) {
		if(resource instanceof IFile) {
			IFile f = (IFile)resource;
			for (int i = 0; i < FILE_SCANNERS.length; i++) {
				IFileScanner scanner = FILE_SCANNERS[i];
				if(scanner.isRelevant(f)) {
					if(!scanner.isLikelyComponentSource(f)) return false;
					LoadedDeclarations c = null;
					try {
						c = scanner.parse(f);
					} catch (Exception e) {
						SeamCorePlugin.getDefault().logError(e);
					}
					if(c != null) componentsLoaded(c, f);
				}
			}
		}
		//return true to continue visiting children.
		return true;
	}
	
	void componentsLoaded(LoadedDeclarations c, IFile resource) {
		if(c == null || c.getComponents().size() + c.getFactories().size() == 0) return;
		p.registerComponents(c, resource.getFullPath());
	}

}
