/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.text.INodeReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeansXMLDefinition {
	static String NODE_INTERCEPTORS = "Interceptors";
	static String NODE_DECORATORS = "Decorators";
	static String NODE_ALTERNATIVES = "Alternatives";
	static String ATTR_CLASS = "class";
	static String ATTR_STEREOTYPE = "stereotype";

	private IPath path;
	private Set<INodeReference> typeAlternatives = new HashSet<INodeReference>();
	private Set<INodeReference> stereotypeAlternatives = new HashSet<INodeReference>();
	private Set<INodeReference> decorators = new HashSet<INodeReference>();
	private Set<INodeReference> interceptors = new HashSet<INodeReference>();

	public BeansXMLDefinition() {}

	public void setBeansXML(XModelObject beansXML) {
		if(beansXML.getModelEntity().getName().startsWith("FileCDIBeans")) {
			if(beansXML instanceof FileAnyImpl) {
				FileAnyImpl f = (FileAnyImpl)beansXML;
				if(f.getParent() instanceof FolderImpl) {
					((FolderImpl)f.getParent()).waitForUpdate(); // I am not sure that we need it, but let this call be here for the sake of testing.
					((FolderImpl)f.getParent()).update();
				}
			}
			XModelObject interceptorsObject = beansXML.getChildByPath(NODE_INTERCEPTORS);
			if(interceptorsObject != null) {
				XModelObject[] cs = interceptorsObject.getChildren();
				for (XModelObject o: cs) {
					interceptors.add(new XMLNodeReference(o, ATTR_CLASS));
				}
			}
			XModelObject decoratorsObject = beansXML.getChildByPath(NODE_DECORATORS);
			if(decoratorsObject != null) {
				XModelObject[] cs = decoratorsObject.getChildren();
				for (XModelObject o: cs) {
					decorators.add(new XMLNodeReference(o, ATTR_CLASS));
				}
			}
			XModelObject alternativesObject = beansXML.getChildByPath(NODE_ALTERNATIVES);
			if(alternativesObject != null) {
				XModelObject[] cs = alternativesObject.getChildren("CDIClass");
				for (XModelObject o: cs) {
					typeAlternatives.add(new XMLNodeReference(o, ATTR_CLASS));
				}
				cs = alternativesObject.getChildren("CDIStereotype");
				for (XModelObject o: cs) {
					stereotypeAlternatives.add(new XMLNodeReference(o, ATTR_STEREOTYPE));
				}
			}

		}
		
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	public Set<INodeReference> getTypeAlternatives() {
		return typeAlternatives;
	}

	public Set<INodeReference> getStereotypeAlternatives() {
		return stereotypeAlternatives;
	}

	public Set<INodeReference> getDecorators() {
		return decorators;
	}

	public Set<INodeReference> getInterceptors() {
		return interceptors;
	}


}
