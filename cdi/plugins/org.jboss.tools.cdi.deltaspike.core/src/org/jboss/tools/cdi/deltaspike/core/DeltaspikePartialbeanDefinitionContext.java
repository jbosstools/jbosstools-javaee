/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
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
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class DeltaspikePartialbeanDefinitionContext extends AbstractDefinitionContextExtension implements DeltaspikeConstants {
	Map<String, DeltaspikePartialbeanBindingConfiguration> partialbeanBindingConfigurations = new HashMap<String, DeltaspikePartialbeanBindingConfiguration>();
	
	protected DeltaspikePartialbeanDefinitionContext copy(boolean clean) {
		DeltaspikePartialbeanDefinitionContext copy = new DeltaspikePartialbeanDefinitionContext();
		copy.root = root;
		if(!clean) {
			copy.partialbeanBindingConfigurations.putAll(partialbeanBindingConfigurations);
		}		
		return copy;
	}

	@Override
	protected void doApplyWorkingCopy() {
		DeltaspikePartialbeanDefinitionContext copy = (DeltaspikePartialbeanDefinitionContext)workingCopy;
		partialbeanBindingConfigurations = copy.partialbeanBindingConfigurations;
	}

	@Override
	public void clean() {
		partialbeanBindingConfigurations.clear();
	}

	@Override
	public void clean(IPath path) {

	}

	@Override
	public void clean(String typeName) {
		partialbeanBindingConfigurations.remove(typeName);
		for (DeltaspikePartialbeanBindingConfiguration c: partialbeanBindingConfigurations.values()) {
			c.clear(typeName);
		}
	}

	@Override
	public void computeAnnotationKind(AnnotationDefinition annotation) {
		if(annotation.isAnnotationPresent(PARTIALBEAN_BINDING_ANNOTATION_TYPE_NAME)) {
			annotation.setExtendedKind(PARTIALBEAN_BINDING_ANNOTATION_KIND);
			String qn = annotation.getType().getFullyQualifiedName();
			DeltaspikePartialbeanBindingConfiguration c = getConfiguration(qn);
			c.setPartialbeanBindingTypeDefinition(annotation, this);
			if(!annotation.getType().isBinary()) {
				IPath newPath = annotation.getType().getResource().getFullPath();
				Set<IPath> ps = c.getInvolvedTypes();
				for (IPath p: ps) {
					getRootContext().addDependency(p, newPath);
					getRootContext().addDependency(newPath, p);
				}
				ps.add(newPath);
			}
		}
	}

	public DeltaspikePartialbeanBindingConfiguration getConfiguration(String typeName) {
		DeltaspikePartialbeanBindingConfiguration result = partialbeanBindingConfigurations.get(typeName);
		if(result == null) {
			result = new DeltaspikePartialbeanBindingConfiguration(typeName);
			partialbeanBindingConfigurations.put(typeName, result);
		}
		return result;
	}

}

