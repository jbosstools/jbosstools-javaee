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
package org.jboss.tools.cdi.seam.config.core.definition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigCorePlugin;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.util.Util;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.TypeDeclaration;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamBeansDefinition {
	IResource resource;
	XModelObject file;
	Set<SAXNodeProblem> unresolvedNodes = new HashSet<SAXNodeProblem>();
	Set<String> possibleTypeNames = new HashSet<String>();

	Set<SeamBeanDefinition> beanDefinitions = new HashSet<SeamBeanDefinition>();
	Set<SeamVirtualFieldDefinition> virtualFieldDefinitions = new HashSet<SeamVirtualFieldDefinition>();

	List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();
	List<IType> replacedAndModified = new ArrayList<IType>();

	public SeamBeansDefinition() {}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public void setFileObject(XModelObject file) {
		this.file = file;
	}

	public XModelObject getFileObject() {
		return file;
	}

	public IResource getResource() {
		return resource;
	}

	public Set<SAXNodeProblem> getUnresolvedNodes() {
		return unresolvedNodes;
	}

	/**
	 * Returns type names that could resolve unresolved nodes if such types existed.
	 * 
	 * @return
	 */
	public Set<String> getPossibleTypeNames() {
		return possibleTypeNames;
	}

	public void addUnresolvedNode(SAXNodeProblem problem) {
		unresolvedNodes.add(problem);
	}

	public void addUnresolvedNode(SAXNode node, String problemId, String message) {
		addUnresolvedNode(new SAXNodeProblem(node, problemId, message));
	}

	public void addPossibleTypeNames(Set<String> types) {
		possibleTypeNames.addAll(types);
	}

	public void addBeanDefinition(SeamBeanDefinition def) {
		beanDefinitions.add(def);
	}

	public Set<SeamBeanDefinition> getBeanDefinitions() {
		return beanDefinitions;
	}

	public void addVirtualField(SeamVirtualFieldDefinition def) {
		virtualFieldDefinitions.add(def);
	}

	public Set<SeamVirtualFieldDefinition> getVirtualFieldDefinitions() {
		return virtualFieldDefinitions;
	}

	public List<TypeDefinition> getTypeDefinitions() {
		return typeDefinitions;
	}

	public void buildTypeDefinitions(ConfigDefinitionContext context) {
		typeDefinitions.clear();

		for (SeamBeanDefinition def: beanDefinitions) {
			IType type = def.getType();
			ConfigTypeDefinition typeDef = new ConfigTypeDefinition();
			typeDef.setFileObject(file);
			boolean replaces = def.getReplacesLocation() != null;
			boolean modifies = def.getModifiesLocation() != null;
			if(replaces || modifies) {
				replacedAndModified.add(type);
				((DefinitionContext)context.getRootContext()).veto(type);
			}
			//Initialize typeDef taking into account replaces and modifies
			int flags = AbstractMemberDefinition.FLAG_ALL_MEMBERS;
			if(!modifies) {
				//For replacing or created - no annotations loaded.
				flags |= AbstractMemberDefinition.FLAG_NO_ANNOTATIONS;
			}
			typeDef.setType(type, context.getRootContext(), flags);
			def.setConfigType(typeDef);

			mergeTypeDefinition(def, typeDef, context);

			typeDefinitions.add(typeDef);
		}
		
		for (SeamVirtualFieldDefinition def: virtualFieldDefinitions) {
			IType type = def.getType();
			ConfigVirtualFieldDefinition typeDef = new ConfigVirtualFieldDefinition();
			typeDef.setFileObject(file);
			typeDef.setConfig(def);
			int flags = AbstractMemberDefinition.FLAG_NO_ANNOTATIONS;
			typeDef.setType(type, context.getRootContext(), flags);
			mergeAnnotations(def, typeDef, context);
			//That is how field producers differ from class beans. They do not need a bean constructor.
			typeDef.setBeanConstructor(true);
			typeDefinitions.add(typeDef);
		}
	}

	public void clean(ConfigDefinitionContext context) {
		List<IType> ds = replacedAndModified;
		replacedAndModified = new ArrayList<IType>();
		for (IType type: ds) {
			((DefinitionContext)context.getRootContext()).unveto(type);
		}
	}

	public SeamMemberDefinition findExactly(int offset) {
		for (SeamBeanDefinition b: beanDefinitions) {
			SeamMemberDefinition d = b.findExactly(offset);
			if(d != null) return d;
		}
		return null;
	}

	private void mergeTypeDefinition(SeamBeanDefinition def, ConfigTypeDefinition typeDef, ConfigDefinitionContext context) {
		typeDef.setConfig(def);
		mergeAnnotations(def, typeDef, context);
		
		List<FieldDefinition> fieldDefs = typeDef.getFields();
		//virtual field definitions are used for injections of inline beans as values of collections and maps.
		List<FieldDefinition> virtualDefs = new ArrayList<FieldDefinition>();
		for (FieldDefinition fieldDef:fieldDefs) {
			String n = fieldDef.getField().getElementName();
			SeamFieldDefinition f = def.getField(n);
			if(f != null) {
				ParametedType t = null;
				IParametedType collection = null;
				IParametedType map = null;
				IParametedType object = null;
				try {
					String returnType = fieldDef.getField().getTypeSignature();
					t = context.getRootContext().getProject().getTypeFactory().getParametedType(fieldDef.getField(), returnType);
					object = context.getRootContext().getProject().getTypeFactory().getParametedType(fieldDef.getField(), "QObject;");
				} catch (CoreException e) {
					CDISeamConfigCorePlugin.getDefault().logError(e);
				}
				if(t != null && t.getType() != null) {
					collection = getCollection(t);
					map = getMap(t);
				}
				List<SeamFieldValueDefinition> vs = f.getValueDefinitions();
				if(collection != null) {
					List<? extends IParametedType> ps = t.getParameters();
					IParametedType elementType = ps.isEmpty() ? object : ps.get(0);
					for (SeamFieldValueDefinition v: vs) {
						ConfigFieldDefinition virtual = new ConfigFieldDefinition(file);
						virtual.setTypeDefinition(fieldDef.getTypeDefinition());
						virtual.setField(fieldDef.getField(), context.getRootContext(), AbstractMemberDefinition.FLAG_NO_ANNOTATIONS);
						virtual.setOverridenType(new TypeDeclaration((ParametedType)elementType, fieldDef.getField().getResource(), 0, 0));
						virtual.setConfig(v);
						virtualDefs.add(virtual);
						v.setRequiredType(elementType);
						mergeAnnotations(v, virtual, context);
					}
				} else if(map != null) {
					List<? extends IParametedType> ps = t.getParameters();
					IParametedType keyType = ps.isEmpty() ? object : ps.get(0);
					IParametedType valueType = ps.size() < 2 ? object : ps.get(1);
					for (SeamFieldValueDefinition v: vs) {
						ConfigFieldDefinition virtual = new ConfigFieldDefinition(file);
						virtual.setTypeDefinition(fieldDef.getTypeDefinition());
						virtual.setField(fieldDef.getField(), context.getRootContext(), AbstractMemberDefinition.FLAG_NO_ANNOTATIONS);
						IParametedType vType = Util.isKey((SAXElement)v.getNode()) ? keyType : valueType;
						v.setRequiredType(vType);
						virtual.setOverridenType(new TypeDeclaration((ParametedType)vType, fieldDef.getField().getResource(), 0, 0));
						virtual.setConfig(v);
						virtualDefs.add(virtual);
						mergeAnnotations(v, virtual, context);
					}
				} else {
					for (SeamFieldValueDefinition v: vs) {
						v.setRequiredType(t);
					}
				}

				((ConfigFieldDefinition)fieldDef).setConfig(f);
				mergeAnnotations(f, fieldDef, context);
				
				if(!vs.isEmpty() && collection == null && map == null) {
					mergeAnnotations(vs.get(0), fieldDef, context);
				}
				
			}
		}
		fieldDefs.addAll(virtualDefs);
	
		List<MethodDefinition> methodDefs = typeDef.getMethods();
		for (MethodDefinition methodDef: methodDefs) {
			IMethod method = methodDef.getMethod();
			if(method == null) continue;
			SeamMethodDefinition m = def.getMethod(method);
			if(m != null) {
				((ConfigMethodDefinition)methodDef).setConfig(m);
				mergeAnnotations(m, methodDef, context);
				List<ParameterDefinition> psDefs = methodDef.getParameters();
				List<SeamParameterDefinition> ps = m.getParameters();
				if(ps.size() != psDefs.size()) {
					System.out.println("different number of parameters in MethodDefinition and SeamMethodDefinition for method " + method.getDeclaringType().getElementName() + "." + method.getElementName());
				} else {
					for (int i = 0; i < ps.size(); i++) {
						mergeAnnotations(ps.get(i), psDefs.get(i), context);
					}
				}
			}
		}
		
		typeDef.checkConstructor();
		
	}

	private void mergeAnnotations(SeamMemberDefinition def, AbstractMemberDefinition memberDef, ConfigDefinitionContext context) {
		Map<String, IJavaAnnotation> annotations = def.getAnnotations();
		for (String typeName: annotations.keySet()) {
			IJavaAnnotation ja = annotations.get(typeName);
			IAnnotationDeclaration current = memberDef.getAnnotation(typeName);
			if(current != null) memberDef.removeAnnotation(current);
			memberDef.addAnnotation(ja, context.getRootContext());
		}
	}

	private IParametedType getCollection(ParametedType t) {
		Set<IParametedType> is = t.getAllTypes();
		for (IParametedType i: is) {
			if("java.util.Collection".equals(i.getType().getFullyQualifiedName())) {
				return i;
			}
		}
		return null;
	}

	private IParametedType getMap(ParametedType t) {
		Set<IParametedType> is = t.getAllTypes();
		for (IParametedType i: is) {
			if("java.util.Map".equals(i.getType().getFullyQualifiedName())) {
				return i;
			}
		}
		return null;
	}

}
