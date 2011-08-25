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
package org.jboss.tools.cdi.seam.config.core.definition;

import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ConfigMethodDefinition extends MethodDefinition implements IConfigDefinition {
	protected SeamMethodDefinition config;
	protected XModelObject file;

	public ConfigMethodDefinition(XModelObject file) {
		this.file = file;
	}

	public void setConfig(SeamMethodDefinition config) {
		this.config = config;
		setOriginalDefinition(new TextSourceReference(config.getResource(), config.getNode()));
	}

	public SeamMethodDefinition getConfig() {
		return config;
	}

}
