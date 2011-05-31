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
package org.jboss.tools.cdi.seam.solder.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.SourceRange;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.solder.core.definition.InterfaceDefinition;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderLoggerExtension implements ICDIExtension, IBuildParticipantFeature {
	CDICoreNature project;
	LoggerDefinitionContext context = new LoggerDefinitionContext();

	public void setProject(CDICoreNature n) {
		project = n;
	}

	public IDefinitionContextExtension getContext() {
		return context;
	}

	public void beginVisiting() {
	}

	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML) {
	}

	public void visit(IFile file, IPath src, IPath webinf) {
	}

	public void buildDefinitions() {
	}

	public void buildDefinitions(FileSet fileSet) {
		LoggerDefinitionContext workingCopy = (LoggerDefinitionContext)context.getWorkingCopy();
		
		Map<IPath, Set<IType>> is = fileSet.getInterfaces();
		for (IPath path: is.keySet()) {
			Set<IType> ts = is.get(path);
			for (IType t: ts) {
				InterfaceDefinition i = new InterfaceDefinition(t, context);
				if(i.isAnnotationPresent(CDISeamSolderConstants.MESSAGE_LOGGER_ANNOTATION_TYPE_NAME)) {
					TypeDefinition d = new TypeDefinition();
					d.setType(t, workingCopy.getRootContext(), 0);
					workingCopy.addMessageLogger(path, d);
				} else if(i.isAnnotationPresent(CDISeamSolderConstants.MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME)) {
					TypeDefinition d = new TypeDefinition();
					d.setType(t, workingCopy.getRootContext(), 0);
					workingCopy.addMessageBundle(path, d);
					AnnotationDeclaration ad = d.getAnnotation(CDISeamSolderConstants.MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME);
					if(ad.getMemberValue("projectCode") != null && ad.getMemberValue("projectCode").toString().length() > 0) {
						String text = d.getContent();
						int st = ad.getStartPosition();
						int le = ad.getLength();
						String source = text.substring(st, st + le);
						AnnotationLiteral l = new AnnotationLiteral(d.getResource(), source, new SourceRange(st, le), null, ad.getType());
						d.removeAnnotation(ad);
						d.addAnnotation(l, workingCopy.getRootContext());
					}				
					
				}
			}
		}
	}

	public void buildBeans() {
		CDIProject p = ((CDIProject)project.getDelegate());
		Map<String, TypeDefinition> loggers = context.getMessageLoggers();
		for (TypeDefinition d: loggers.values()) {
			ClassBean b = new ClassBean();
			b.setDefinition(d);
			b.setParent(p);
			p.addBean(b);
		}

		Map<String, TypeDefinition> bundles = context.getMessageBundles();
		for (TypeDefinition d: bundles.values()) {
			ClassBean b = new ClassBean();
			b.setDefinition(d);
			b.setParent(p);
			p.addBean(b);
		}

	}

	class LoggerDefinitionContext extends AbstractDefinitionContextExtension {
		Map<String, TypeDefinition> messageLoggers = new HashMap<String, TypeDefinition>();
		Map<String, TypeDefinition> messageBundles = new HashMap<String, TypeDefinition>();
	

		protected LoggerDefinitionContext copy(boolean clean) {
			LoggerDefinitionContext copy = new LoggerDefinitionContext();
			copy.root = root;
			if(!clean) {
				copy.messageLoggers.putAll(messageLoggers);
				copy.messageBundles.putAll(messageBundles);
			}

			return copy;
		}

		@Override
		protected void doApplyWorkingCopy() {
			messageLoggers = ((LoggerDefinitionContext)workingCopy).messageLoggers;
			messageBundles = ((LoggerDefinitionContext)workingCopy).messageBundles;
		}

		@Override
		public void clean() {
			messageLoggers.clear();
			messageBundles.clear();			
		}

		@Override
		public void clean(IPath path) {
		}

		@Override
		public void clean(String typeName) {
			messageLoggers.remove(typeName);
			messageBundles.remove(typeName);
		}

		public void addMessageLogger(IPath path, TypeDefinition def) {
			String typeName = def.getType().getFullyQualifiedName();
			messageLoggers.put(typeName, def);
			root.addType(path, typeName);
		}
		
		public void addMessageBundle(IPath path, TypeDefinition def) {
			String typeName = def.getType().getFullyQualifiedName();
			messageBundles.put(typeName, def);
			root.addType(path, typeName);
		}
	
		public Map<String, TypeDefinition> getMessageLoggers() {
			return messageLoggers;
		}
		
		public Map<String, TypeDefinition> getMessageBundles() {
			return messageBundles;
		}
		
	}

}
