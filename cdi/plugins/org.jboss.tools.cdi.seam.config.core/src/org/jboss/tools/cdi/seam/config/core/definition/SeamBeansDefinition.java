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

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.scanner.SAXNode;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamBeansDefinition {
	Map<SAXNode, String> unresolvedNodes = new HashMap<SAXNode, String>();

	Set<SeamBeanDefinition> beanDefinitions = new HashSet<SeamBeanDefinition>();
	Set<SeamVirtualFieldDefinition> virtualFieldDefinitions = new HashSet<SeamVirtualFieldDefinition>();

	List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();
	List<IType> replacedAndModified = new ArrayList<IType>();
	
	public SeamBeansDefinition() {}

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
		for (SeamBeanDefinition def: beanDefinitions) {
			IType type = def.getType();
			TypeDefinition typeDef = new TypeDefinition();
			boolean replaces = def.getReplacesLocation() != null;
			boolean modifies = def.getModifiesLocation() != null;
			if(replaces || modifies) {
				//TODO veto type in root context
			}
			//TODO Initialize typeDef taking into account replaces and modifies
			typeDef.setType(type, context.getRootContext());
			//TODO merge seam definitions into typeDef and add to typeDefinitions
		}		
	}

	public void clean(ConfigDefinitionContext context) {
		List<IType> ds = replacedAndModified;
		replacedAndModified = new ArrayList<IType>();
		for (IType type: ds) {
			//TODO unveto type in root context
		}
	}

}
