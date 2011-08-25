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

import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ConfigVirtualFieldDefinition extends TypeDefinition implements IConfigDefinition {
	protected SeamVirtualFieldDefinition config;
	protected XModelObject file;

	public ConfigVirtualFieldDefinition() {}

	public void setConfig(SeamVirtualFieldDefinition config) {
		this.config = config;
		setOriginalDefinition(new TextSourceReference(config.getResource(), config.getNode()));
	}

	public void setFileObject(XModelObject file) {
		this.file = file;
	}

	public SeamVirtualFieldDefinition getConfig() {
		return config;
	}

	protected FieldDefinition newFieldDefinition() {
		return new ConfigFieldDefinition(file);
	}

	protected MethodDefinition newMethodDefinition() {
		return new ConfigMethodDefinition(file);
	}

}
