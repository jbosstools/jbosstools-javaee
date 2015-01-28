/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.internal.core;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBatchArtifact {

	public IPath getSourcePath();

	/**
	 * Returns one of types listed in enum BatchArtifactType.
	 * @return
	 */
	public BatchArtifactType getArtifactType();

	/**
	 * Returns Named declaration at Java field. May return null.
	 * 
	 * @return
	 */
	public IAnnotationDeclaration getNamedDeclaration();
	/**
	 * Returns name that may be set by
	 * 1. javax.inject.Named
	 * 2. batch.xml
	 * 3. default - qualified class name.
	 * Never returns null.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns Java type representing the artifact.
	 * @return
	 */
	public IType getType();

	/**
	 * Returns list of batch properties.
	 * @return
	 */
	public Collection<IBatchProperty> getProperties();
}
