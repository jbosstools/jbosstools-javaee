/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractTypeDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.common.CommonPlugin;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaReference;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.TypeDeclaration;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class AbstractBeanElement extends CDIElement implements IAnnotated {
	protected AbstractMemberDefinition definition;

	public AbstractBeanElement() {}

	@Override
	public boolean exists() {
		return getDefinition().exists();
	}

	public void setDefinition(AbstractMemberDefinition definition) {
		this.definition = definition;
	}

	public AbstractMemberDefinition getDefinition() {
		return definition;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotations()
	 */
	public List<IAnnotationDeclaration> getAnnotations() {
		if(definition!=null) {
			return definition.getAnnotations();
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotation(java.lang.String)
	 */
	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		if(definition!=null) {
			return definition.getAnnotation(annotationTypeName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotationPosition(java.lang.String)
	 */
	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
		return getAnnotation(annotationTypeName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#isAnnotationPresent(java.lang.String)
	 */
	public boolean isAnnotationPresent(String annotationTypeName) {
		return definition!=null && definition.isAnnotationPresent(annotationTypeName);
	}

	protected AnnotationDeclaration findNamedAnnotation() {
		AnnotationDeclaration named = getDefinition().getNamedAnnotation();
		if(named != null) return named;
		for (IStereotypeDeclaration d: getStereotypeDeclarations(true)) {
			StereotypeElement s = (StereotypeElement)d.getStereotype();
			if(s != null && s.getNameDeclaration() != null) return s.getNameDeclaration();
		}
		return null;
	}

	public boolean isAlternative() {
		if(getDefinition().getAlternativeAnnotation() != null) return true;
		for (IStereotypeDeclaration d: getStereotypeDeclarations()) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative()) return true;
		}		
		return false;
	}

	public Collection<IStereotypeDeclaration> getStereotypeDeclarations() {
		return getStereotypeDeclarations(false);
	}

	public Collection<IStereotypeDeclaration> getStereotypeDeclarations(boolean includeInherited) {
		Set<IStereotypeDeclaration> result = new HashSet<IStereotypeDeclaration>();
		Set<IStereotype> ss = new HashSet<IStereotype>();
		for (IAnnotationDeclaration d: definition.getAnnotations()) {
			if(d instanceof IStereotypeDeclaration) {
				IStereotypeDeclaration sd = (IStereotypeDeclaration)d;
				result.add(sd);
				if(sd.getStereotype() != null) ss.add(sd.getStereotype());
			}
		}
		Set<IStereotypeDeclaration> delta1 = result;
		Set<IStereotypeDeclaration> delta2 = new HashSet<IStereotypeDeclaration>();
		while(!delta1.isEmpty()) {
			for (IStereotypeDeclaration d: delta1) {
				IStereotype s = d.getStereotype();
				if(s == null) continue;
				for (IStereotypeDeclaration d1: s.getStereotypeDeclarations()) {
					if(d1.getStereotype() != null) {
						if(!result.contains(d1) && !delta2.contains(d1)) delta2.add(d1);
					}
				}
			}
			if(delta2.isEmpty()) break;
			for (IStereotypeDeclaration d: delta2) {
				result.add(d);
				if(d.getStereotype() != null) ss.add(d.getStereotype());
			}
			delta1 = delta2;
			delta2 = new HashSet<IStereotypeDeclaration>();
		}
		if(includeInherited) {
			Set<IStereotypeDeclaration> ds = getInheritedStereotypDeclarations();
			for (IStereotypeDeclaration d : ds) {
				if (d.getStereotype() != null && !ss.contains(d.getStereotype())) {
					result.add(d);
				}
			}
		}
		return result;
	}

	public Set<IStereotypeDeclaration> getInheritedStereotypDeclarations() {
		return Collections.emptySet();
	}

	public Collection<IQualifierDeclaration> getQualifierDeclarations() {
		return getQualifierDeclarations(false);
	}

	public Collection<IQualifierDeclaration> getQualifierDeclarations(boolean includeInherited) {
		Collection<IQualifierDeclaration> result = new ArrayList<IQualifierDeclaration>();
		Set<IQualifier> qs = new HashSet<IQualifier>();
		for(IAnnotationDeclaration a: definition.getAnnotations()) {
			if(a instanceof IQualifierDeclaration) {
				IQualifierDeclaration q = (IQualifierDeclaration)a;
				result.add(q);
				if(q.getQualifier() != null) qs.add(q.getQualifier());				
			}
		}
		if(includeInherited) {
			for (IQualifierDeclaration d : getInheritedQualifierDeclarations()) {
				if (d.getQualifier() != null && !qs.contains(d.getQualifier())) {
					result.add(d);
				}
			}
// JBIDE-11623 @Named in stereotype is not used as a bean qualifier.
// Do not add it to the qualifier list.
		}
		return result;
	}

	protected Collection<IQualifierDeclaration> getInheritedQualifierDeclarations() {
		return Collections.emptyList();
	}

	protected Collection<IInterceptorBindingDeclaration> getInheritedInterceptorBindingDeclarations() {
		return Collections.emptyList();
	}

	public Collection<IQualifier> getQualifiers() {
		IQualifier any = getCDIProject().getQualifier(CDIConstants.ANY_QUALIFIER_TYPE_NAME);
		IQualifier def = getCDIProject().getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		IQualifier name = getCDIProject().getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);

		Set<IQualifier> result = new HashSet<IQualifier>();
		for (IQualifierDeclaration d: getQualifierDeclarations(true)) {
			IQualifier q = d.getQualifier();
			if(q != null) result.add(q);
		}
		if(this instanceof IInjectionPoint) {
			if(def != null && result.isEmpty()) {
				result.add(def);
			}
		} else if(this instanceof IBean) {
			if(def != null) {
				if(result.isEmpty() || (name != null && result.size() == 1 && result.contains(name))) {
					result.add(def);
				}
			}			
			if(any != null) result.add(any);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IClassBean#getInterceptorBindingDeclarations()
	 */
	public Collection<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations(boolean includeInherited) {
		Collection<IInterceptorBindingDeclaration> result = ClassBean.getInterceptorBindingDeclarations(definition);
		if(includeInherited) {
			Set<IInterceptorBinding> qs = new HashSet<IInterceptorBinding>();
			for (IInterceptorBindingDeclaration d: result) {
				if(d.getInterceptorBinding() != null) qs.add(d.getInterceptorBinding());
			}
			for (IInterceptorBindingDeclaration d : getInheritedInterceptorBindingDeclarations()) {
				if (d.getInterceptorBinding() != null && !qs.contains(d.getInterceptorBinding())) {
					result.add(d);
				}
			}
		}
		return result;
	}

	public Collection<IScopeDeclaration> getScopeDeclarations() {
		return getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

	public static Collection<IScopeDeclaration> getScopeDeclarations(CDICoreNature n, List<? extends IAnnotationDeclaration> ds) {
		Collection<IScopeDeclaration> result = new ArrayList<IScopeDeclaration>(1);
		for (IAnnotationDeclaration d: ds) {
			int k = n.getDefinitions().getAnnotationKind(d.getType());
			if(k > 0 && (k & AnnotationDefinition.SCOPE) > 0 && d instanceof IScopeDeclaration) {
				result.add((IScopeDeclaration)d);
			}
		}
		return result;
	}

	public Collection<ITypeDeclaration> getRestrictedTypeDeclarations(Collection<IParametedType> alltypes) {
		AnnotationDeclaration typed = getDefinition().getTypedAnnotation();
		if(typed == null) {
			return new ArrayList<ITypeDeclaration>(0);
		}
		Map<String, IParametedType> map = new HashMap<String, IParametedType>();
		for (IParametedType t: alltypes) {
			map.put(t.getType().getFullyQualifiedName(), t);
		}
		Collection<ITypeDeclaration> result = new ArrayList<ITypeDeclaration>();
		int s = typed.getStartPosition();
		int l = typed.getLength();
		try {
			String txt = null;
			if(s >= 0 && typed.getResource() instanceof IFile) {
				AbstractTypeDefinition td = getDefinition().getTypeDefinition();
				if(getDefinition().getOriginalDefinition() != null) {
					ITextSourceReference r = getDefinition().getOriginalDefinition();
					String content = FileUtil.readStream((IFile)r.getResource());
					if(content != null && content.length() > s + l) {
						txt = content.substring(s);
					}
				} else if(td != null) {
					String content = td.getContent();
					if(content != null && content.length() > s + l) {
						txt = content.substring(s, s + l);
					}
				}
			}
			
			Object value = typed.getMemberValue(null);
			if(value == null) return result;
			IMember member = (IMember)definition.getMember();
			IType declaringType = member instanceof IType ? (IType)member : member.getDeclaringType();
			if(value instanceof Object[]) {
				Object[] os = (Object[])value;
				for (int i = 0; i < os.length; i++) {
					String rawTypeName = os[i].toString();
					String typeName = rawTypeName;
					if(!typeName.endsWith(";")) typeName = "Q" + typeName + ";";
					ParametedType p = getCDIProject().getNature().getTypeFactory().getParametedType(declaringType, typeName);
					if(p != null) {
						int offset = 0;
						int length = 0;
						if(txt != null) {
							int q = txt.indexOf(rawTypeName);
							if(q >= 0) {
								offset = s + q;
								length = rawTypeName.length();
							}
						}
						IParametedType other = p.getType() == null ? null : map.get(p.getType().getFullyQualifiedName());
						if(other != null) {
							String s1 = p.getSignature();
							String s2 = other.getSignature();
							if(!s1.equals(s2) && Signature.getArrayCount(s1) == Signature.getArrayCount(s2)) {
								p.setSignature(s2);
							}
							result.add(new TypeDeclaration((ParametedType)other, typed.getResource(), offset, length));
						} else {
							result.add(new TypeDeclaration(p, typed.getResource(), offset, length));
						}
					}
				}
			} else if(value != null) {
				String rawTypeName = value.toString();
				String typeName = rawTypeName;
				if(!typeName.endsWith(";")) typeName = "Q" + typeName + ";";
				ParametedType p = getCDIProject().getNature().getTypeFactory().getParametedType(declaringType, typeName);
				if(p != null) {
					int offset = 0;
					int length = 0;
					if(txt != null) {
						int q = txt.indexOf(rawTypeName);
						if(q >= 0) {
							offset = s + q;
							length = rawTypeName.length();
						}
					}
					IParametedType other = p.getType() == null ? null : map.get(p.getType().getFullyQualifiedName());
					if(other != null) {
						String s1 = p.getSignature();
						String s2 = other.getSignature();
						if(!s1.equals(s2) && Signature.getArrayCount(s1) == Signature.getArrayCount(s2)) {
							p.setSignature(s2);
						}
						result.add(new TypeDeclaration((ParametedType)other, typed.getResource(), offset, length));
					} else {
						result.add(new TypeDeclaration(p, typed.getResource(), offset, length));
					}
				}
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return result;
	}

	public void open() {
		if(getDefinition().getOriginalDefinition() != null) {
			IEditorPart part = null;
			ITextSourceReference source = getDefinition().getOriginalDefinition();
			IFile resource = (IFile)source.getResource();
			IWorkbenchWindow window = CDICorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (window == null)	return;
			IWorkbenchPage page = window.getActivePage();
			try {
				part = IDE.openEditor(page, resource);
			} catch (PartInitException e) {
				CDICorePlugin.getDefault().logError(e);
			}
			if(part instanceof EditorPartWrapper) {
				part = ((EditorPartWrapper)part).getEditor();
			}
			if(part instanceof ObjectMultiPageEditor) {
				ObjectMultiPageEditor mpe = (ObjectMultiPageEditor)part;
				ITextEditor textEditor = (ITextEditor)mpe.getAdapter(ITextEditor.class);
				if(textEditor != null) {
					mpe.setActiveEditor(textEditor);
					part = textEditor;
				}
			}
			if(part != null) {
				part.getEditorSite().getSelectionProvider().setSelection(new TextSelection(source.getStartPosition(), source.getLength()));
			}
		} else if (this instanceof IJavaReference) {
			IMember member = ((IJavaReference)this).getSourceMember();
			try {
				JavaUI.openInEditor(member);
			} catch (PartInitException e) {
				CommonPlugin.getDefault().logError(e);
			} catch (JavaModelException e) {
				CommonPlugin.getDefault().logError(e);
			}
		}
	}
	
}
