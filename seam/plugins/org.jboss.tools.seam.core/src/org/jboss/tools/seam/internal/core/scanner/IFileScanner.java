/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.scanner;

import org.eclipse.core.resources.IFile;

public interface IFileScanner {

	/**
	 * First, the most trivial check by file mask.
	 * If it returns false, other scanners will be invoked.
	 * If it returns true, other scanners will NOT be invoked.
	 * @param resource
	 * @return
	 */
	public boolean isRelevant(IFile resource);
	
	/**
	 * Second, more detailed check of file content. 
	 * @param f
	 * @return
	 */
	public boolean isLikelyComponentSource(IFile f);

	/**
	 * Loading components declared in resource.
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public LoadedDeclarations parse(IFile f) throws Exception;

}
