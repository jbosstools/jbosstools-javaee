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
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.internal.IKbProjectExtension;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBatchProject extends IKbProjectExtension {

	/**
	 * Returns all artifacts declared in this project.
	 * @return
	 */
	public Collection<IBatchArtifact> getAllArtifacts();

	/**
	 * Returns Batch artifact model objects declared in the .java resource or empty collection.
	 * @param resource
	 * @return
	 */
	public Collection<IBatchArtifact> getArtifacts(IResource resource);

	/**
	 * Returns Batch artifact model objects for the given artifact type or empty collection.
	 * @param type
	 * @return
	 */
	public Collection<IBatchArtifact> getArtifacts(BatchArtifactType artifactType);

	/**
	 * Returns Batch artifact model objects for the name or empty collection.
	 * Artifact name should be unique. We have collection for validation purposes.
	 * @param name
	 * @return
	 */
	public Collection<IBatchArtifact> getArtifacts(String name);

	/**
	 * Returns Batch artifact model object for the Java type or null.
	 * @param name
	 * @return
	 */
	public IBatchArtifact getArtifact(IType type);

	/**
	 * Returns set of batch jobs declared in the current project.
	 * @return
	 */
	public Set<IFile> getDeclaredBatchJobs();

	/**
	 * Returns references to type in batch resources by fully qualified name.
	 * 
	 * @param type
	 * @return
	 */
	public Collection<ITextSourceReference> getReferences(IType type);

//	public Set<? extends IBatchProject> getUsedProjects();

}
