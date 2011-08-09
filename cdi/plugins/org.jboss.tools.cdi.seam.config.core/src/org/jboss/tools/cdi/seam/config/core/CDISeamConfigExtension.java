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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.TextSourceReference;
import org.jboss.tools.cdi.seam.config.core.scanner.ConfigFileSet;
import org.jboss.tools.cdi.seam.config.core.scanner.SeamDefinitionBuilder;
import org.jboss.tools.cdi.seam.config.core.validation.SeamConfigValidationMessages;
import org.jboss.tools.cdi.seam.config.core.xml.SAXAttribute;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamConfigExtension implements ICDIExtension, IBuildParticipantFeature, IValidatorFeature {
	ConfigDefinitionContext context = new ConfigDefinitionContext();

	ConfigFileSet fileSet = new ConfigFileSet();

	public static CDISeamConfigExtension getExtension(CDICoreNature project) {
		Set<ICDIExtension> es = project.getExtensionManager().getExtensions(IBuildParticipantFeature.class);
		for (ICDIExtension ext: es) {
			if(ext instanceof CDISeamConfigExtension) return (CDISeamConfigExtension)ext;
		}
		return null;
	}

	public ConfigDefinitionContext getContext() {
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
		List<SeamBeansDefinition> newDefinitions = new ArrayList<SeamBeansDefinition>();
		 Set<IPath> paths = fileSet.getAllPaths();
		 for (IPath p: paths) {
			 boolean isSeamBeans = false;
			 XModelObject o = fileSet.getBeanXML(p);
			 if(o == null) {
				 o = fileSet.getSeamBeanXML(p);
				 isSeamBeans = true;
			 }
			 if(o instanceof FileAnyImpl) {
				 FileAnyImpl f = (FileAnyImpl)o;
				 if(f.getParent() instanceof FolderImpl) {
					 ((FolderImpl)f.getParent()).update();
				 }
				 String text = f.getAsText();
				 IResource resource = (IResource)o.getAdapter(IResource.class);
				 IDocument document = new Document();
				 SeamDefinitionBuilder builder = new SeamDefinitionBuilder();
				 document.set(text);
				 SeamBeansDefinition def = builder.createDefinition(resource, document, context.getRootContext().getProject(), context.getWorkingCopy());
				 newDefinitions.add(def);
				 if(isSeamBeans) {
					 context.getWorkingCopy().addSeamBeanXML(p, def);
				 } else {
					 context.getWorkingCopy().addBeanXML(p, def);
				 }
			 }
		 }
		 
		 for (SeamBeansDefinition def: newDefinitions) {
			 //Or, should we just build through all context?
			 def.buildTypeDefinitions(context.getWorkingCopy());
		 }
		//TODO
	}

	public void buildDefinitions(FileSet fileSet) {
		//nothing to do since we visited all resources.
	}

	public void buildBeans(CDIProject target) {
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

	public void validateResource(IFile file, CDICoreValidator validator) {
		SeamBeansDefinition def = context.getDefinition(file.getFullPath());
		if(def != null) {
			Map<SAXNode, String> nodes = def.getUnresolvedNodes();
			for (Entry<SAXNode, String> entry: nodes.entrySet()) {
				SAXNode node = entry.getKey();
				String problemId = entry.getValue();
				if(CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE.equals(problemId)) {
					String name = node instanceof SAXElement ? ((SAXElement)node).getName() : node instanceof SAXAttribute ? ((SAXAttribute)node).getName() : null;
					String message = NLS.bind(SeamConfigValidationMessages.UNRESOLVED_TYPE, name);
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_TYPE, new TextSourceReference(file, node), file);
				} else if(CDISeamConfigConstants.ERROR_UNRESOLVED_MEMBER.equals(problemId)) {
					String name = node instanceof SAXElement ? ((SAXElement)node).getName() : node instanceof SAXAttribute ? ((SAXAttribute)node).getName() : null;
					String message = NLS.bind(SeamConfigValidationMessages.UNRESOLVED_MEMBER, name);
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_MEMBER, new TextSourceReference(file, node), file);
				} else if(CDISeamConfigConstants.ERROR_UNRESOLVED_METHOD.equals(problemId)) {
					String message = NLS.bind(SeamConfigValidationMessages.UNRESOLVED_METHOD, ((SAXElement)node).getName());
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_METHOD, new TextSourceReference(file, node), file);
				} else if(CDISeamConfigConstants.ERROR_UNRESOLVED_CONSTRUCTOR.equals(problemId)) {
					String name = node instanceof SAXElement && ((SAXElement)node).getParent() != null ? ((SAXElement)node).getParent().getName() : null;
					String message = NLS.bind(SeamConfigValidationMessages.UNRESOLVED_CONSTRUCTOR, name);
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_CONSTRUCTOR, new TextSourceReference(file, node), file);
				} else if(CDISeamConfigConstants.ERROR_ANNOTATION_EXPECTED.equals(problemId)) {
					String message = NLS.bind(SeamConfigValidationMessages.ANNOTATION_EXPECTED, null);
					validator.addError(message, CDISeamConfigPreferences.ANNOTATION_EXPECTED, new TextSourceReference(file, node), file);
				}
			}
		}
	}

	public SeverityPreferences getSeverityPreferences() {
		return CDISeamConfigPreferences.getInstance();
	}

}
