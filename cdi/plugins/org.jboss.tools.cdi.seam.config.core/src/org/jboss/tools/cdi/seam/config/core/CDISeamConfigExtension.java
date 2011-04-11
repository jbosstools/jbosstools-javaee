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
package org.jboss.tools.cdi.seam.config.core;


import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.scanner.ConfigFileSet;
import org.jboss.tools.cdi.seam.config.core.scanner.SeamDefinitionBuilder;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamConfigExtension implements ICDIExtension, IBuildParticipantFeature {
	CDICoreNature project;
	ConfigDefinitionContext context = new ConfigDefinitionContext();

	ConfigFileSet fileSet = new ConfigFileSet();

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void setProject(CDICoreNature n) {
		project = n;
	}

	public IDefinitionContextExtension getContext() {
		return context;
	}

	public void beginVisiting() {
		fileSet = new ConfigFileSet();
	}

	public void visit(IFile file, IPath src, IPath webinf) {
		IPath path = file.getFullPath();
		if(src != null && path.segmentCount() == src.segmentCount() + 2
				&& "META-INF".equals(path.segments()[path.segmentCount() - 2])) {
			addBeansXML(file, fileSet);
		} else if(webinf != null && webinf.isPrefixOf(path) && webinf.segmentCount() == path.segmentCount() - 1) {
			addBeansXML(file, fileSet);
		}
	}

	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML) {
		if(beansXML != null) {
			fileSet.setBeanXML(path, beansXML);
			XModelObject seamBeanXML = beansXML.getParent().getChildByPath(CDISeamConfigConstants.SEAM_BEANS_XML);
			if(seamBeanXML != null) {
				fileSet.setSeamBeanXML(path, seamBeanXML);
			}
		}
	}

	public void buildDefinitions() {
		 Set<IPath> paths = fileSet.getAllPaths();
		 for (IPath p: paths) {
			 boolean isSeamBeans = false;
			 XModelObject o = fileSet.getBeanXML(p);
			 if(o == null) {
				 o = fileSet.getSeamBeanXML(p);
				 isSeamBeans = true;
			 }
			 if(o instanceof FileAnyImpl) {
				 String text = ((FileAnyImpl)o).getAsText();
				 IDocument document = new Document();
				 SeamDefinitionBuilder builder = new SeamDefinitionBuilder();
				 document.set(text);
				 SeamBeansDefinition def = builder.createDefinition(document, project);
				 if(isSeamBeans) {
					 context.getWorkingCopy().addSeamBeanXML(p, def);
				 } else {
					 context.getWorkingCopy().addBeanXML(p, def);
				 }
			 }
		 }
		//TODO
	}

	public void buildDefinitions(FileSet fileSet) {
		//nothing to do
	}

	public void buildBeans() {
		//TODO
	}

	private void addBeansXML(IFile f, ConfigFileSet fileSet) {
		if(f.getName().equals("beans.xml")) {
			XModelObject beansXML = getObject(f);
			if(beansXML != null) {
				fileSet.setBeanXML(f.getFullPath(), beansXML);
			}
		} else if(f.getName().equals(CDISeamConfigConstants.SEAM_BEANS_XML)) {
			XModelObject beansXML = getObject(f);
			if(beansXML != null) {
				fileSet.setSeamBeanXML(f.getFullPath(), beansXML);
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

}
