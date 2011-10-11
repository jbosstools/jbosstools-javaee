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
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.seam.config.core.definition.ConfigTypeDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SAXNodeProblem;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeanDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldValueDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.TextSourceReference;
import org.jboss.tools.cdi.seam.config.core.scanner.ConfigFileSet;
import org.jboss.tools.cdi.seam.config.core.scanner.SeamDefinitionBuilder;
import org.jboss.tools.cdi.seam.config.core.validation.SeamConfigValidationMessages;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamConfigExtension implements ICDIExtension, IBuildParticipantFeature, IValidatorFeature {
	private static String ID = "org.jboss.solder.config.xml.bootstrap.XmlConfigExtension"; 
	private static String ID_30 = "org.jboss.seam.config.xml.bootstrap.XmlConfigExtension"; 
	ConfigDefinitionContext context = new ConfigDefinitionContext();

	ConfigFileSet fileSet = new ConfigFileSet();

	public static CDISeamConfigExtension getExtension(CDICoreNature project) {
		ICDIExtension result = project.getExtensionManager().getExtensionByRuntime(ID);
		if(result == null) {
			result = project.getExtensionManager().getExtensionByRuntime(ID_30);
		}
		if(result instanceof CDISeamConfigExtension) {
			return (CDISeamConfigExtension)result;
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
		} else if(src != null && file.getName().endsWith(".java")) {
			//Check that Java type appeared that may resolve a node in seam beans xml. 
			try {
				ICompilationUnit unit = EclipseUtil.getCompilationUnit(file);
				if(unit != null) {
					IType[] ts = unit.getTypes();
					for (IType t: ts) {
						String type = t.getFullyQualifiedName();
						IPath p = context.getWorkingCopy().getPathForPossibleType(type);
						if(p != null && fileSet.getBeanXML(p) == null) {
							IFile f = context.getRootContext().getProject().getProject().getWorkspace().getRoot().getFile(p);
							if(f.exists()) {
								addBeansXML(f, fileSet);
							}
						}
					}
				}
			} catch (CoreException e) {
				CDISeamConfigCorePlugin.getDefault().logError(e);
			}
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
				 def.setFileObject(o);
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
			Set<SAXNodeProblem> nodes = def.getUnresolvedNodes();
			for (SAXNodeProblem problem: nodes) {
				SAXNode node = problem.getNode();
				String problemId = problem.getProblemId();
				String message = problem.getMessage();
				ITextSourceReference ref = new TextSourceReference(def.getFileObject(), file, node);
				if(CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE.equals(problemId)) {
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_TYPE, ref, file);
				} else if(CDISeamConfigConstants.ERROR_UNRESOLVED_MEMBER.equals(problemId)) {
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_MEMBER, ref, file);
				} else if(CDISeamConfigConstants.ERROR_UNRESOLVED_METHOD.equals(problemId)) {
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_METHOD, ref, file);
				} else if(CDISeamConfigConstants.ERROR_UNRESOLVED_CONSTRUCTOR.equals(problemId)) {
					validator.addError(message, CDISeamConfigPreferences.UNRESOLVED_CONSTRUCTOR, ref, file);
				} else if(CDISeamConfigConstants.ERROR_ANNOTATION_EXPECTED.equals(problemId)) {
					validator.addError(message, CDISeamConfigPreferences.ANNOTATION_EXPECTED, ref, file);
				}
			}
			Set<SeamBeanDefinition> bs = def.getBeanDefinitions();
			for (SeamBeanDefinition b: bs) {
				List<SeamFieldDefinition> fs = b.getFields();
				for (SeamFieldDefinition f: fs) {
					List<SeamFieldValueDefinition> vs = f.getValueDefinitions();
					if(vs.isEmpty()) continue;
					for (SeamFieldValueDefinition v: vs) {
						IParametedType requiredType = v.getRequiredType();
						SeamBeanDefinition inline = v.getInlineBean();
						ConfigTypeDefinition d = inline.getConfigType();
						IParametedType actualType = d.getParametedType();
						if(requiredType != null && actualType != null) {
							if(!((ParametedType)actualType).isAssignableTo((ParametedType)requiredType, true)) {
								String actual = actualType.getSimpleName();
								String required = requiredType.getSimpleName();
								String message = NLS.bind(SeamConfigValidationMessages.INLINE_BEAN_TYPE_MISMATCH, actual, required);
								validator.addError(message, CDISeamConfigPreferences.INLINE_BEAN_TYPE_MISMATCH, new TextSourceReference(def.getFileObject(), file, inline.getNode()), file);
							}
						}
					}
				}
			}
			List<TypeDefinition> ds = def.getTypeDefinitions();
			for (TypeDefinition d: ds) {
				if(!d.hasBeanConstructor()) {
					ConfigTypeDefinition cd = (ConfigTypeDefinition)d;
					SAXNode n = cd.getConfig().getNode();
					if(d.isAbstract()) {
						String message = NLS.bind(SeamConfigValidationMessages.TYPE_IS_ABSTRACT, cd.getParametedType().getSimpleName());
						validator.addError(message, CDISeamConfigPreferences.ABSTRACT_TYPE_IS_CONFIGURED_AS_BEAN, new TextSourceReference(def.getFileObject(), file, n), file);
					} else {
						String message = NLS.bind(SeamConfigValidationMessages.NO_BEAN_CONSTRUCTOR, cd.getParametedType().getSimpleName());
						validator.addError(message, CDISeamConfigPreferences.BEAN_CONSTRUCTOR_IS_MISSING, new TextSourceReference(def.getFileObject(), file, n), file);
					}
				}
			}
		}
	}

	public SeverityPreferences getSeverityPreferences() {
		return CDISeamConfigPreferences.getInstance();
	}

}
