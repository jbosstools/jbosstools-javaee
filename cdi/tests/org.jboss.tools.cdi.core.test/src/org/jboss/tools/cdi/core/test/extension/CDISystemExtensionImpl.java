/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.extension;

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
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISystemExtensionImpl implements ICDIExtension, IBuildParticipantFeature {
	SystemContext systemContext = new SystemContext();

	public CDISystemExtensionImpl() {}

	@Override
	public IDefinitionContextExtension getContext() {
		return systemContext;
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

	static final String MY_BEAN_INTERFACE = "cdi.test.extension.MyBeanInterface";

	@Override
	public void buildDefinitions() {
		IType type = systemContext.getRootContext().getProject().getType(MY_BEAN_INTERFACE);
		if(type != null && type.exists()) {
			if(systemContext.myBeanDefinition == null ||
					systemContext.myBeanDefinition.getType() != type) {
				systemContext.myBeanPath = type.getPath();
				systemContext.myBeanDefinition = new TypeDefinition();
				systemContext.myBeanDefinition.setType(type, systemContext.getRootContext(), 0);
				systemContext.myBeanDefinition.setBeanConstructor(true);
				((DefinitionContext)systemContext.getRootContext().getWorkingCopy()).addType(type.getPath(), type.getFullyQualifiedName(), systemContext.myBeanDefinition);
			}
		} else {
			if(systemContext.myBeanPath != null) {
				systemContext.getRootContext().clean(systemContext.myBeanPath);
				systemContext.myBeanPath = null;
			}
			systemContext.myBeanDefinition = null;
		}		
	}

	@Override
	public void buildDefinitions(FileSet fileSet) {
	}

	@Override
	public void buildBeans(CDIProject target) {
	}

	class SystemContext extends AbstractDefinitionContextExtension {
		IPath myBeanPath = null;
		TypeDefinition myBeanDefinition = null;

		protected SystemContext copy(boolean clean) {
			SystemContext copy = new SystemContext();
			copy.root = root;
			if(!clean) {
				copy.myBeanDefinition = myBeanDefinition;
			}
			return copy;
		}
		
		public void clean() {
			myBeanPath = null;
			myBeanDefinition = null;
		}

	}

}
