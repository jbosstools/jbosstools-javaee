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
package org.jboss.tools.jsf.jsf2.bean.model;

import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.jst.web.kb.internal.IKbProjectExtension;

public interface IJSF2Project extends IKbProjectExtension {

	/**
	 * Returns all managed beans declared in Java classes with @ManagedBeans annotation
	 * 
	 * @return
	 */
	public Set<IJSF2ManagedBean> getManagedBeans();

	/**
	 * Returns JSF2 managed beans declared in resource at given path.
	 * 
	 * @param path
	 * @return
	 */
	public Set<IJSF2ManagedBean> getManagedBeans(IPath path);

	/**
	 * Returns JSF2 managed beans with given name.
	 * 
	 * @param name
	 * @return
	 */
	public Set<IJSF2ManagedBean> getManagedBeans(String name);

	/**
	 * Returns true, if file /WEB-INF/faces-config.xml exist and declares
	 * metadata-complete="true", otherwise returns false.
	 * 
	 * When metadata is complete, all managed beans loaded from annotations are disabled.
	 * 
	 * @return
	 */
	public boolean isMetadataComplete();

	/**
	 * Cleans from model objects loaded at given path.
	 * @param path
	 */
	public void pathRemoved(IPath path);

}
