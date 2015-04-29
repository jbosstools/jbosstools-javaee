/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.extension.internal.core.jms;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.common.model.XModelObject;

/**
 * @author Viacheslav Kabanovich
 */
public class JMSContextExtension implements ICDIExtension, IBuildParticipantFeature {
	JMSContext jmsContext = new JMSContext();

	public JMSContextExtension() {}

	@Override
	public IDefinitionContextExtension getContext() {
		return jmsContext;
	}

	@Override
	public void beginVisiting() {
	}

	@Override
	public void visitJar(IPath path, IPackageFragmentRoot root,
			XModelObject beansXML) {
	}

	@Override
	public void visit(IFile file, IPath src, IPath webinf) {
	}

	static final String PRODUCER_BEAN_TYPE = "org.jboss.as.messaging.deployment.JMSContextProducer";

	@Override
	public void buildDefinitions() {
		IType type = jmsContext.getRootContext().getProject().getType(PRODUCER_BEAN_TYPE);
		if(type != null && type.exists()) {
			jmsContext.setType(type);
		} else {
			jmsContext.cleanPath();
		}
		
	}

	@Override
	public void buildDefinitions(FileSet fileSet) {
	}

	@Override
	public void buildBeans(CDIProject target) {
	}

	class JMSContext extends AbstractDefinitionContextExtension {
		IPath beanPath = null;
		TypeDefinition beanDefinition = null;

		protected JMSContext copy(boolean clean) {
			JMSContext copy = new JMSContext();
			copy.root = root;
			if(!clean) {
				copy.beanPath = beanPath;
				copy.beanDefinition = beanDefinition;
			}
			return copy;
		}
		
		public void clean() {
			beanPath = null;
			beanDefinition = null;
		}

		public void setType(IType type) {
			if(beanDefinition == null || beanDefinition.getType() != type) {
				beanPath = type.getPath();
				beanDefinition = new TypeDefinition();
				beanDefinition.setType(type, getRootContext(), 0);
				beanDefinition.setBeanConstructor(true);
				((DefinitionContext)getRootContext().getWorkingCopy()).addType(type.getPath(), type.getFullyQualifiedName(), beanDefinition);
			}
		}
	
		public void cleanPath() {
			if(beanPath != null) {
				getRootContext().clean(beanPath);
				beanPath = null;
				beanDefinition = null;
			}
		}

	}

}
