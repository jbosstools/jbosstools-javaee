/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipant2Feature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.core.international.impl.BundleModel;
import org.jboss.tools.cdi.seam.core.international.scanner.BundleFileSet;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * 
 * @author Viacheslav Kabanlvich
 *
 */
public class CDISeamInternationalExtension implements ICDIExtension, IBuildParticipant2Feature {
	CDISeamInternationalDefinitionContext context = new CDISeamInternationalDefinitionContext();
	BundleFileSet fileSet = new BundleFileSet();

	BundleModel model = new BundleModel();

	public static CDISeamInternationalExtension getExtension(CDICoreNature project) {
		Set<ICDIExtension> es = project.getExtensionManager().getExtensions(IBuildParticipantFeature.class);
		for (ICDIExtension ext: es) {
			if(ext instanceof CDISeamInternationalExtension) return (CDISeamInternationalExtension)ext;
		}
		return null;
	}

	public BundleModel getBundleModel() {
		return model;
	}

	public CDISeamInternationalDefinitionContext getContext() {
		return context;
	}

	public void beginVisiting() {
		fileSet = new BundleFileSet();
	}

	public void visit(IFile file, IPath src, IPath webinf) {
		IPath path = file.getFullPath();
		if(src != null && src.isPrefixOf(path)) {
			addBundle(file, fileSet);
		}
	}

	private void addBundle(IFile f, BundleFileSet fileSet) {
		if(f.getName().endsWith(".properties")) {
			XModelObject b = getObject(f);
			if(b != null) {
				fileSet.setBundle(f.getFullPath(), b);
			}
		}
	}

	private XModelObject getObject(IFile f) {
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		if(o == null) {
			o = EclipseResourceUtil.createObjectForResource(f);
		}
		return o;
	}

	@Override
	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML) {
	}

	@Override
	public void buildDefinitions() {
		((CDISeamInternationalDefinitionContext)context.getWorkingCopy()).addDefinitions(fileSet);
	}

	@Override
	public void buildDefinitions(FileSet fileSet) {
	}

	@Override
	public void buildBeans(CDIProject target) {
		model.rebuild(context.getAllBundles());
	}

	@Override
	public void visitJar(IPath path, XModelObject fs) {
		Set<XModelObject> objects = collectXModelBundleObjects(fs, null);
		fileSet.setBundles(path, objects);
	}

	private Set<XModelObject> collectXModelBundleObjects(XModelObject o, Set<XModelObject> objects) {
		if(objects == null) {
			objects = new HashSet<XModelObject>();
		}
		if (o == null) return objects;
		
		String path = o.getPath();
		if (path == null || "META-INF".equalsIgnoreCase(o.getAttributeValue("name")))
			return objects;
		
		if (path.endsWith(".properties")) {
			objects.add(o);
		}

		if (o.getFileType() > XModelObject.FILE && o.hasChildren()) {
			XModelObject[] children = o.getChildren();
			if (children != null) {
				for (XModelObject c : children) {
					collectXModelBundleObjects(c, objects);
				}
			}
		}
		return objects;
	}
}
