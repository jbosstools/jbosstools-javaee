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
package org.jboss.tools.batch.internal.core.impl.definition;

import java.util.Map;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchXMLDefinition {
	IPath path;
	IFile file;

	Map<String, String> classNameToArtifactName = new HashMap<String, String>();

	public BatchXMLDefinition() {}

	public void setPath(IPath path) {
		this.path = path;
	}

	public void setFile(IFile file) {
		this.file = file;
		this.path = file.getFullPath();
	}

	public IPath getPath() {
		return path;
	}

	public IFile getFile() {
		return file;
	}

	/**
	 * Returns mapping of fully qualified class names to Batch artifact names. 
	 * @return
	 */
	public Map<String, String> getMapping() {
		return classNameToArtifactName;
	}

	public synchronized String getArtifactName(String className) {
		synchronized (this) {
			return classNameToArtifactName.get(className);
		}
	}

	public synchronized void add(String className, String artifactName) {
		classNameToArtifactName.put(className, artifactName);
	}

}
