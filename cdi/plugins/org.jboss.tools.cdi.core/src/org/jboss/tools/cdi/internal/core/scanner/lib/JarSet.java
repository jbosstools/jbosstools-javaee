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
package org.jboss.tools.cdi.internal.core.scanner.lib;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanlvich
 *
 */
public class JarSet {
	Map<String, XModelObject> fileSystems = new HashMap<String, XModelObject>();
	Map<String, XModelObject> beanModules = new HashMap<String, XModelObject>();
	
	public Map<String, XModelObject> getBeanModules() {
		return beanModules;
	}

	public Map<String, XModelObject> getFileSystems() {
		return fileSystems;
	}

}
