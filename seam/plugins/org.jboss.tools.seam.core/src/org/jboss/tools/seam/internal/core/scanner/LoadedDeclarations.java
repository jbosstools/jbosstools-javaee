/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.scanner;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamNamespace;
import org.jboss.tools.seam.internal.core.SeamImport;

/**
 * This object keeps all declarations loaded from one source.
 * 
 * @author Viacheslav Kabanovich
 */
public class LoadedDeclarations {
	List<ISeamComponentDeclaration> components = new ArrayList<ISeamComponentDeclaration>();
	List<ISeamFactory> factories = new ArrayList<ISeamFactory>();
	List<SeamImport> imports = new ArrayList<SeamImport>();
	List<ISeamNamespace> namespaces = new ArrayList<ISeamNamespace>();
	
	public List<ISeamComponentDeclaration> getComponents() {
		return components;
	}
	
	public List<ISeamFactory> getFactories() {
		return factories;
	}
	
	public List<SeamImport> getImports() {
		return imports;
	}

	public List<ISeamNamespace> getNamespaces() {
		return namespaces;
	}
	
	public void add(LoadedDeclarations ds) {
		if(ds == null) return;
		components.addAll(ds.components);
		factories.addAll(ds.factories);
		imports.addAll(ds.imports);
		namespaces.addAll(ds.namespaces);
	}

}
