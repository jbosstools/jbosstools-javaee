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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IAmbiguousBeanResolverFeature;
import org.jboss.tools.cdi.core.extension.feature.IBeanKeyProvider;
import org.jboss.tools.cdi.core.extension.feature.IBeanStoreFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.internal.core.impl.BeanMember;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.seam.solder.core.validation.SeamSolderValidationMessages;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.java.IJavaReference;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.java.impl.AnnotationLiteral;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * Implements support for org.jboss.seam.solder.bean.defaultbean.DefaultBeanExtension.
 * 
 * In processing annotated type adds to each bean definition, which is a default bean, 
 * faked @Typed annotation with type set by @DefaultBean.
 * 
 * In resolving ambiguous beans removes default beans out of the result set if it 
 * contains at least one non-default bean;
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderDefaultBeanExtension implements ICDIExtension, IProcessAnnotatedTypeFeature, IAmbiguousBeanResolverFeature, IValidatorFeature, IBeanKeyProvider, IBeanStoreFeature {
	private static String ID = "org.jboss.solder.bean.defaultbean.DefaultBeanExtension"; //$NON-NLS-1$
	private static String ID_30 = "org.jboss.seam.solder.bean.defaultbean.DefaultBeanExtension"; //$NON-NLS-1$
	
	protected Map<String, Set<IBean>> defaultBeansByKey = new HashMap<String, Set<IBean>>();

	public static CDISeamSolderDefaultBeanExtension getExtension(CDICoreNature project) {
		ICDIExtension result = project.getExtensionManager().getExtensionByRuntime(ID);
		if(result == null) {
			result = project.getExtensionManager().getExtensionByRuntime(ID_30);
		}
		if(result instanceof CDISeamSolderDefaultBeanExtension) {
			return (CDISeamSolderDefaultBeanExtension)result;
		}
		return null;
	}

	protected Version getVersion() {
		return Version.instance;
	}

	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		String defaultBeanAnnotationTypeName = getVersion().getDefaultBeanAnnotationTypeName();
		boolean defaultBean = typeDefinition.isAnnotationPresent(defaultBeanAnnotationTypeName);
		IJavaAnnotation beanTyped = null;
		if(defaultBean) {
			beanTyped = createFakeTypedAnnotation(typeDefinition, context);
			if(beanTyped != null) {
				typeDefinition.addAnnotation(beanTyped, context);
			}
		}
		List<MethodDefinition> ms = typeDefinition.getMethods();
		for (MethodDefinition m: ms) {
			if(m.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)) {
				if(defaultBean || m.isAnnotationPresent(defaultBeanAnnotationTypeName)) {
					IJavaAnnotation methodTyped = createFakeTypedAnnotation(m, context);
					if(methodTyped != null) {
						m.addAnnotation(methodTyped, context);
					}
				}
			}
		}
		List<FieldDefinition> fs = typeDefinition.getFields();
		for (FieldDefinition f: fs) {
			if(f.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)) {
				if(defaultBean || f.isAnnotationPresent(defaultBeanAnnotationTypeName)) {
					IJavaAnnotation fieldTyped = createFakeTypedAnnotation(f, context);
					if(fieldTyped != null) {
						f.addAnnotation(fieldTyped, context);
					}
				}
			}
		}
	}

	IJavaAnnotation createFakeTypedAnnotation(AbstractMemberDefinition def, IRootDefinitionContext context) {
		IJavaAnnotation result = null;
		IAnnotationDeclaration a = def.getAnnotation(getVersion().getDefaultBeanAnnotationTypeName());
		if(a != null) {
			Object n = a.getMemberValue(null);
			if(n != null && n.toString().length() > 0) {
				String defaultType = n.toString();
				IType typedAnnotation = context.getProject().getType(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
				if (typedAnnotation != null) { 
					result = new AnnotationLiteral(def.getResource(), a.getStartPosition(), a.getLength(), defaultType, IMemberValuePair.K_CLASS, typedAnnotation);
				}
			}
		} else if(def instanceof BeanMemberDefinition) {
			ITypeDeclaration type = BeanMember.getTypeDeclaration(def, context.getProject().getTypeFactory());
			if(type != null) {
				IType typedAnnotation = context.getProject().getType(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
				if (typedAnnotation != null) { 
					result = new AnnotationLiteral(def.getResource(), type.getStartPosition(), type.getLength(), type.getType().getFullyQualifiedName(), IMemberValuePair.K_CLASS, typedAnnotation);
				}
			}
		}
		return result;
	 
	}

	public Set<IBean> getResolvedBeans(Set<IBean> result) {
		Set<IBean> defaultBeans = new HashSet<IBean>();
		for (IBean b: result) {
			if(isBeanDefault(b)) {
				defaultBeans.add(b);
			}
		}
		if(!defaultBeans.isEmpty() && defaultBeans.size() < result.size()) {
			result.removeAll(defaultBeans);
		}
		return result;
	}

	public boolean isBeanDefault(IBean bean) {
		String defaultBeanAnnotationTypeName = getVersion().getDefaultBeanAnnotationTypeName();
		if(bean.isAnnotationPresent(defaultBeanAnnotationTypeName)) {
			return true;
		} else if(bean instanceof IProducer) {
			IProducer producer = (IProducer)bean;
			IClassBean parent = producer.getClassBean();
			if(parent != null && parent.isAnnotationPresent(defaultBeanAnnotationTypeName)) {
				return true;
			}
		}
		return false;
	}

	public void validateResource(IFile file, CDICoreValidator validator) {
		String defaultBeanAnnotationTypeName = getVersion().getDefaultBeanAnnotationTypeName();
		ICDIProject cdiProject = CDICorePlugin.getCDIProject(file.getProject(), true);
		Set<IBean> bs = cdiProject.getBeans(file.getFullPath());
		for (IBean bean: bs) {
			if(isBeanDefault(bean)) {
				ITextSourceReference a = bean.getAnnotation(defaultBeanAnnotationTypeName);
				if(a == null) {
					Set<ITypeDeclaration> ds = bean.getAllTypeDeclarations();
					if(!ds.isEmpty()) {
						IMember e = bean instanceof IJavaReference ? ((IJavaReference)bean).getSourceMember() : bean.getBeanClass();
						a = CDIUtil.convertToJavaSourceReference(ds.iterator().next(), e);
					} else {
						continue;
					}
				}
				if(bean instanceof IProducerField) {
					IClassBean cb = ((IProducerField) bean).getClassBean();
					IScope scope = cb.getScope();
					if(scope != null && scope.isNorlmalScope()) {
						validator.addError(SeamSolderValidationMessages.DEFAULT_PRODUCER_FIELD_ON_NORMAL_SCOPED_BEAN, 
								CDISeamSolderPreferences.DEFAULT_PRODUCER_FIELD_ON_NORMAL_SCOPED_BEAN, new String[]{}, a, file);
					}
				}
				IQualifierDeclaration[] qs = bean.getQualifierDeclarations().toArray(new IQualifierDeclaration[0]);
				IParametedType type = getDefaultType(bean);
				if(type != null) {
					String key = createKey(type, bean.getQualifierDeclarations(true));
					Set<IBean> linked = defaultBeansByKey.get(key);
					if(linked != null) {
						for (IBean link: linked) {
							if(link.getSourcePath() != null) {
								validator.getValidationContext().addLinkedCoreResource(CDICoreValidator.SHORT_ID, key, link.getSourcePath(), true);
							}
						}
					}
					Set<IBean> bs2 = cdiProject.getBeans(false, type, qs);
					StringBuilder otherDefaultBeans = new StringBuilder();
					for (IBean b: bs2) {
						try {
						if(b != bean && isBeanDefault(b)
								&& CDIProject.areMatchingQualifiers(bean.getQualifierDeclarations(), b.getQualifierDeclarations(true))) {
							if(otherDefaultBeans.length() > 0) {
								otherDefaultBeans.append(", ");
							}
							otherDefaultBeans.append(b.getElementName());
						}
						} catch (CoreException e) {
							CDISeamSolderCorePlugin.getDefault().logError(e);
						}
					}
					if(otherDefaultBeans.length() > 0) {
						String message = NLS.bind(SeamSolderValidationMessages.IDENTICAL_DEFAULT_BEANS, otherDefaultBeans);
						validator.addError(message, CDISeamSolderPreferences.IDENTICAL_DEFAULT_BEANS, new String[]{}, a, file);
					}
				}
			}
		}
	}

	public SeverityPreferences getSeverityPreferences() {
		return CDISeamSolderPreferences.getInstance();
	}

	private IParametedType getDefaultType(IBean bean) {
		Set<IParametedType> ts = bean.getLegalTypes();
		if(ts.size() < 3) {
			for (IParametedType t: ts) {
				if(!"java.lang.Object".equals(t.getType().getFullyQualifiedName())) {
					return t;
				}
			}
		}
		return null;
	}

	@Override
	public String getKey(IBean bean) {
		if(isBeanDefault(bean)) {
			IParametedType type = getDefaultType(bean);
			if(type != null) {
				return createKey(type, bean.getQualifierDeclarations(true));
			}
		}		
		return null;
	}

	private String createKey(IParametedType type, Set<IQualifierDeclaration> qs) {
		Set<String> ss = new TreeSet<String>();
		for (IQualifierDeclaration q: qs) {
			if(!q.getTypeName().equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME)
					&& !q.getTypeName().equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)) {
				ss.add(q.getTypeName());
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("#DefaultBean_").append(type.getType().getFullyQualifiedName());
		for (String s: ss) {
			sb.append(':').append(s);
		}
		return sb.toString();
	}

	public synchronized void updateCaches(ICDIProject project) {
		Map<String, Set<IBean>> map = new HashMap<String, Set<IBean>>();

		IBean[] beans = project.getBeans();
		for (IBean b: beans) {
			String key = getKey(b);
			if(key != null) {
				Set<IBean> bs = map.get(key);
				if(bs == null) {
					bs = new HashSet<IBean>();
					map.put(key, bs);
				}
				bs.add(b);
			}
		}
		defaultBeansByKey = map;
	}

}
