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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchJobDefinition {
	IPath path;
	IFile file;

	String jobID = "";

	public BatchJobDefinition() {}

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

	public void setJobID(String id) {
		jobID = id;
	}

	public String getJobID() {
		return jobID;
	}

}
