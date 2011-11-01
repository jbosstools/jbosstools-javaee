 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.common.model.XModelObject;

/**
 * @author Viacheslav Kabanovich
 */
public interface ISeamMessages extends ISeamContextVariable {
	
	/**
	 * 
	 * @return collection of keys in resource bundles 
	 */
	public Collection<String> getPropertyNames();

	/**
	 * 
	 * @return collection of properties declared in resource bundles
	 */
	public Collection<ISeamProperty> getProperties();

	/**
	 * Intruduced instead of getProperties() - as much more lightweight in implementation.
	 * 
	 * @return
	 */
	public Map<String, List<XModelObject>> getPropertiesMap();

}
