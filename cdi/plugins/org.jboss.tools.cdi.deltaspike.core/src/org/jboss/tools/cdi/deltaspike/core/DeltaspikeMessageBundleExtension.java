/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.solder.core.definition.InterfaceDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.model.XModelObject;

/**
 * Runtime
 * org.apache.deltaspike.core.impl.message.MessageBundleExtension
 * 
 * @author Viacheslav Kabanovich
 */
public class DeltaspikeMessageBundleExtension implements ICDIExtension, IBuildParticipantFeature, DeltaspikeConstants {
	DeltaspikeMessageBundleDefinitionContext context = new DeltaspikeMessageBundleDefinitionContext();

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
		DeltaspikeMessageBundleDefinitionContext workingCopy = (DeltaspikeMessageBundleDefinitionContext)context.getWorkingCopy();
		
		Map<IPath, Set<IType>> is = fileSet.getInterfaces();
		for (IPath path: is.keySet()) {
			Set<IType> ts = is.get(path);
			for (IType t: ts) {
				InterfaceDefinition i = new InterfaceDefinition(t, context);
				List<IAnnotationDeclaration> as = i.getAnnotations();
				for (IAnnotationDeclaration a: as) {
					if(workingCopy.isMessageBundleAnnotation(a.getType())) {
						TypeDefinition d = new TypeDefinition();
						d.setType(t, workingCopy.getRootContext(), 0);
						d.setBeanConstructor(true);
						workingCopy.addMessageBundle(path, d);
					}
				}
			}
		}
	}

	public void buildBeans(CDIProject target) {
		CDIProject p = target;
		Map<String, TypeDefinition> services = context.getMessageBundles();
		for (TypeDefinition d: services.values()) {
			ClassBean b = new ClassBean();
			b.setDefinition(d);
			b.setParent(p);
			p.addBean(b);
		}

	}

	class DeltaspikeMessageBundleDefinitionContext extends AbstractDefinitionContextExtension {
		Set<String> messageBundleAnnotations = new HashSet<String>();
		Map<String, TypeDefinition> messageBundles = new HashMap<String, TypeDefinition>();
	

		protected DeltaspikeMessageBundleDefinitionContext copy(boolean clean) {
			DeltaspikeMessageBundleDefinitionContext copy = new DeltaspikeMessageBundleDefinitionContext();
			copy.root = root;
			if(!clean) {
				for (String qn: messageBundles.keySet()) {
					TypeDefinition d = messageBundles.get(qn);
					if(d.exists()) {
						copy.messageBundles.put(qn, d);
					}
				}
				copy.messageBundleAnnotations.addAll(messageBundleAnnotations);
			}

			return copy;
		}

		@Override
		protected void doApplyWorkingCopy() {
			messageBundles = ((DeltaspikeMessageBundleDefinitionContext)workingCopy).messageBundles;
			messageBundleAnnotations = ((DeltaspikeMessageBundleDefinitionContext)workingCopy).messageBundleAnnotations;
		}

		@Override
		public void clean() {
			messageBundles.clear();
			messageBundleAnnotations.clear();
		}

		@Override
		public void clean(String typeName) {
			messageBundles.remove(typeName);
			messageBundleAnnotations.remove(typeName);
		}

		public void addMessageBundle(IPath path, TypeDefinition def) {
			String typeName = def.getType().getFullyQualifiedName();
			messageBundles.put(typeName, def);
			root.addType(path, typeName);
		}
		
		public Map<String, TypeDefinition> getMessageBundles() {
			return messageBundles;
		}

		@Override
		public void computeAnnotationKind(AnnotationDefinition annotation) {
			if(annotation.isAnnotationPresent(MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME)) {
				annotation.setExtendedKind(MESSAGE_BUNDLE_ANNOTATION_KIND);
				messageBundleAnnotations.add(annotation.getType().getFullyQualifiedName());
			}
		}

		public boolean isMessageBundleAnnotation(IType type) {
			return (type != null && 
					(messageBundleAnnotations.contains(type.getFullyQualifiedName()) || MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME.equals(type.getFullyQualifiedName())));
		}
	}
}
