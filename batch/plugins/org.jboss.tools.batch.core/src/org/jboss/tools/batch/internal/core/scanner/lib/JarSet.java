/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.internal.core.scanner.lib;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Viacheslav Kabanlvich
 *
 */
public class JarSet {
	Set<String> fileSystems = new HashSet<String>();
	Set<String> batchModules = new HashSet<String>();
	
	public Set<String> getBatchModules() {
		return batchModules;
	}

	public Set<String> getFileSystems() {
		return fileSystems;
	}

}
