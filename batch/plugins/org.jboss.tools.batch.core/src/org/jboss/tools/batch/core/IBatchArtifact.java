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
package org.jboss.tools.batch.core;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBatchArtifact {

	/**
	 * Returns Batch project object.	 * 
	 * @return
	 */
	public IBatchProject getProject();

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

	/**
	 * Returns batch property by its name or null.
	 * @param name
	 * @return
	 */
	public IBatchProperty getProperty(String name);

	/**
	 * Returns batch property by IField object
	 * @param field
	 * @return
	 */
	public IBatchProperty getProperty(IField field);

	/**
	 * Returns references to this artifact by its name returned by getName() method,
	 * scanning all job XML files declared by the current project.
	 * At present, implementation does the scan at every request to avoid it at build.
	 * 
	 * @return
	 */
	public Collection<ITextSourceReference> getReferences();
}
