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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.xml.Location;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamBeansDefinition {
	IResource resource;
	Map<SAXNode, String> unresolvedNodes = new HashMap<SAXNode, String>();

	Set<SeamBeanDefinition> beanDefinitions = new HashSet<SeamBeanDefinition>();
	Set<SeamVirtualFieldDefinition> virtualFieldDefinitions = new HashSet<SeamVirtualFieldDefinition>();

	List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();
	List<IType> replacedAndModified = new ArrayList<IType>();
	
	public SeamBeansDefinition() {}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public IResource getResource() {
		return resource;
	}

	public Map<SAXNode, String> getUnresolvedNodes() {
		return unresolvedNodes;
	}

	public void addUnresolvedNode(SAXNode node, String problem) {
		unresolvedNodes.put(node, problem);
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
			TypeDefinition typeDef = new TypeDefinition();
			boolean replaces = def.getReplacesLocation() != null;
			boolean modifies = def.getModifiesLocation() != null;
			if(replaces || modifies) {
				replacedAndModified.add(type);
				((DefinitionContext)context.getRootContext()).veto(type);
			}
			//Initialize typeDef taking into account replaces and modifies
			int flags = AbstractMemberDefinition.FLAG_ALL_MEMBERS;
			if(replaces) flags |= AbstractMemberDefinition.FLAG_NO_ANNOTATIONS;
			typeDef.setType(type, context.getRootContext(), flags);

			mergeTypeDefinition(def, typeDef, context);

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

	private void mergeTypeDefinition(SeamBeanDefinition def, TypeDefinition typeDef, ConfigDefinitionContext context) {
		mergeAnnotations(def, typeDef, context);
		
		List<FieldDefinition> fieldDefs = typeDef.getFields();
		for (FieldDefinition fieldDef:fieldDefs) {
			if(fieldDef.getField() == null) continue;
			String n = fieldDef.getField().getElementName();
			SeamFieldDefinition f = def.getField(n);
			if(f != null) {
				fieldDef.setOriginalDefinition(new TextSourceReference(f.getNode().getLocation()));
				mergeAnnotations(f, fieldDef, context);
			}
		}
	
		List<MethodDefinition> methodDefs = typeDef.getMethods();
		for (MethodDefinition methodDef: methodDefs) {
			IMethod method = methodDef.getMethod();
			if(method == null) continue;
			SeamMethodDefinition m = def.getMethod(method);
			if(m != null) {
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

	class TextSourceReference implements ITextSourceReference {
		Location location;
		
		public TextSourceReference(Location location) {
			this.location = location;
		}		

		public int getStartPosition() {
			return location.getStartPosition();
		}

		public int getLength() {
			return location.getLength();
		}

		public IResource getResource() {
			return resource;
		}
		
	}

}
