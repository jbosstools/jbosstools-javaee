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
package org.jboss.tools.cdi.seam.solder.core.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderConstants;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderCorePlugin;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderGenericBeanExtension implements ICDIExtension, IBuildParticipantFeature, IProcessAnnotatedTypeFeature, CDISeamSolderConstants {
	CDICoreNature project;
	GenericBeanDefinitionContext context = new GenericBeanDefinitionContext();

	public void setProject(CDICoreNature n) {
		project = n;
	}

	public IDefinitionContextExtension getContext() {
		return context;
	}

	public void beginVisiting() {
	}

	public void visitJar(IPath path, IPackageFragmentRoot root,	XModelObject beansXML) {
	}

	public void visit(IFile file, IPath src, IPath webinf) {
	}

	public void buildDefinitions() {
	}

	public void buildDefinitions(FileSet fileSet) {
	}

	public void buildBeans() {
		CDIProject p = ((CDIProject)project.getDelegate());

		for (GenericConfiguration c: context.getGenericConfigurations().values()) {	
			Map<AbstractMemberDefinition, List<IAnnotationDeclaration>> ms = c.getGenericProducerBeans();

			//TODO scopes!
			
			Set<TypeDefinition> ts = c.getGenericConfigurationBeans();
			for (List<IAnnotationDeclaration> list: ms.values()) {
				for (TypeDefinition t: ts) {
					TypeDefinition ti = new TypeDefinition();
					ti.setType(t.getType(), context.getRootContext(), 0);
					List<MethodDefinition> ps = ti.getMethods();
					for (MethodDefinition m: ps) {
						if(m.isAnnotationPresent(PRODUCES_ANNOTATION_TYPE_NAME)) {
							for (IAnnotationDeclaration d: list) {
								m.addAnnotation(((AnnotationDeclaration)d).getDeclaration(), context.getRootContext());
							}
						}
					}
					ClassBean cb = new ClassBean();
					cb.setParent(p);
					cb.setDefinition(ti);
					Set<IProducer> producers = cb.getProducers();
					for (IProducer producer: producers) {
						p.addBean(producer);
					}
				}
			}
		}
	}

	@Override
	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		if(typeDefinition.isAnnotationPresent(GENERIC_CONFIGURATION_ANNOTATION_TYPE_NAME)) {
			IAnnotationDeclaration d = typeDefinition.getAnnotation(GENERIC_CONFIGURATION_ANNOTATION_TYPE_NAME);
			Object o = d.getMemberValue(null);
			if(o != null) {
				String s = o.toString();
				if(s.length() > 0) {
					try {
						ParametedType p = context.getProject().getTypeFactory().getParametedType(typeDefinition.getType(), "Q" + s + ";");
						if(p != null && p.getType() != null) {
							GenericConfiguration c = ((GenericBeanDefinitionContext)this.context.getWorkingCopy()).getGenericConfiguration(p.getType().getFullyQualifiedName());
							c.getGenericConfigurationBeans().add(typeDefinition);
						}
					} catch (JavaModelException e) {
						CDISeamSolderCorePlugin.getDefault().logError(e);
					}
				}				
			}
			List<MethodDefinition> ms = typeDefinition.getMethods();
			for (MethodDefinition m: ms) {
				if(m.isAnnotationPresent(PRODUCES_ANNOTATION_TYPE_NAME)) {
					IType q = project.getType(GENERIC_QUALIFIER_TYPE_NAME);
					if(q != null) {
						AnnotationLiteral a = new AnnotationLiteral(m.getResource(), 0, 0, null, 0, q);
						m.addAnnotation(a, context);
					}
				}
			}
		} else {
			IAnnotationDeclaration d = findAnnotationAnnotatedWithGenericType(typeDefinition);
			if(d != null) {
				addGenericProducerBean(typeDefinition, findAnnotationAnnotatedWithGenericType(typeDefinition).getTypeName());
			}
			List<MethodDefinition> ms = typeDefinition.getMethods();
			for (MethodDefinition m: ms) {
				if(m.isAnnotationPresent(PRODUCES_ANNOTATION_TYPE_NAME)) {
					d = findAnnotationAnnotatedWithGenericType(m);
					if(d != null) {
						addGenericProducerBean(m, d.getTypeName());
					}
				}
			}
			List<FieldDefinition> fs = typeDefinition.getFields();
			for (FieldDefinition f: fs) {
				if(f.isAnnotationPresent(PRODUCES_ANNOTATION_TYPE_NAME)) {
					d = findAnnotationAnnotatedWithGenericType(f);
					if(d != null) {
						addGenericProducerBean(f, d.getTypeName());
					}
				}
			}
		}
		
	}

	private void addGenericProducerBean(AbstractMemberDefinition def, String genericType) {
		GenericConfiguration c = ((GenericBeanDefinitionContext)this.context.getWorkingCopy()).getGenericConfiguration(genericType);

		List<IAnnotationDeclaration> list = new ArrayList<IAnnotationDeclaration>();
		List<IAnnotationDeclaration> ds = def.getAnnotations();
		for (IAnnotationDeclaration d: ds) {
			if(d instanceof IQualifierDeclaration) {
				list.add(d);
			}
		}
		c.getGenericProducerBeans().put(def, list);
		if(c.getGenericProducerBeans().size() == 1) {
			IType q = project.getType(GENERIC_QUALIFIER_TYPE_NAME);
			if(q != null) {
				AnnotationLiteral a = new AnnotationLiteral(def.getResource(), 0, 0, null, 0, q);
				def.addAnnotation(a, context.getRootContext());
			}
		}
		IResource r = def.getResource();
		if(r != null && r.exists() && !c.getInvolvedTypes().contains(r.getFullPath())) {
			IPath newPath = r.getFullPath();
			Set<IPath> ps = c.getInvolvedTypes();
			for (IPath p: ps) {
				context.getRootContext().addDependency(p, newPath);
				context.getRootContext().addDependency(newPath, p);
			}
			ps.add(newPath);				
		}
	}

	private IAnnotationDeclaration findAnnotationAnnotatedWithGenericType(AbstractMemberDefinition m) {
		List<IAnnotationDeclaration> ds = m.getAnnotations();
		for (IAnnotationDeclaration d: ds) {
			if(d.getTypeName() != null) {
				AnnotationDefinition a = context.getRootContext().getAnnotation(d.getTypeName());
				if(a != null && a.isAnnotationPresent(GENERIC_TYPE_ANNOTATION_TYPE_NAME)) {
					return d;
				}
			}
		}
		return null;
	}	
	
}
