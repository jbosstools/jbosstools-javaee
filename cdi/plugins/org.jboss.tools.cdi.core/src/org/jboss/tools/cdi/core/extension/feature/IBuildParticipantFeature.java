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
package org.jboss.tools.cdi.core.extension.feature;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.common.model.XModelObject;

/**
 * This feature includes BeforeBeanDiscovery event of CDI runtime, but also it should provide
 * facilities for incremental build and clean of project at design time. 
 * 
 * Method buildBeans() corresponds to AfterBeanDiscovery event of CDI runtime. 
 * It is added here because it seams obvious that extensions which participate
 * in bean discovering would apply result to build CDI artifacts.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBuildParticipantFeature extends ICDIFeature {

	/**
	 * Sets CDI project access object once per lifetime of this object.
	 * @param n
	 */
	public void setProject(CDICoreNature n);

	/**
	 * Implementation should create and keep one instance of IDefinitionContextExtension.
	 * 
	 * @return
	 */
	public IDefinitionContextExtension getContext();

	/**
	 * Prepares storage of bean sources.
	 */
	public void beginVisiting();

	/**
	 * Looks for jar entries that are bean sources.
	 * 
	 * @param path
	 * @param beansXML
	 */
	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML);

	/**
	 * Adds file to discovered bean sources if relevant.
	 * 
	 * @param file
	 * @param src parent Java source folder path or null
	 * @param webinf parent WEB-INF folder path or null
	 */
	public void visit(IFile file, IPath src, IPath webinf);

	/**
	 * Builds specific models for discovered bean sources.
	 * This method is the final stage of BeforeBeanDiscovery event.
	 */
	public void buildDefinitions();

	/**
	 * Build specific models for default bean sources.
	 * That may allow to go without implementing visitor methods 
	 * 
	 * @param fileSet
	 */
	public void buildDefinitions(FileSet fileSet);

	/**
	 * Builds CDI artifacts by definitions and already existing CDI model.
	 * 
	 * This method corresponds to AfterBeanDiscovery event.
	 */
	public void buildBeans();

}
