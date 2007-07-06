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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;

/**
 * This object keeps all declarations loaded from one source.
 * 
 * @author Viacheslav Kabanovich
 */
public class LoadedDeclarations {
	List<SeamComponentDeclaration> components = new ArrayList<SeamComponentDeclaration>();
	List<ISeamFactory> factories = new ArrayList<ISeamFactory>();
	
	public List<SeamComponentDeclaration> getComponents() {
		return components;
	}
	
	public List<ISeamFactory> getFactories() {
		return factories;
	}
	
	public void add(LoadedDeclarations ds) {
		if(ds == null) return;
		components.addAll(ds.components);
		factories.addAll(ds.factories);
	}

}
