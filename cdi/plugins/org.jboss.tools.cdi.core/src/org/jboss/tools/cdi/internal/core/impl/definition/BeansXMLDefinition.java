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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.internal.core.impl.Excluded;
import org.jboss.tools.cdi.xml.beans.model.CDIBeansConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.text.INodeReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeansXMLDefinition implements CDIBeansConstants {

	private IPath path;
	private Collection<INodeReference> typeAlternatives = new ArrayList<INodeReference>();
	private Collection<INodeReference> stereotypeAlternatives = new ArrayList<INodeReference>();
	private Collection<INodeReference> decorators = new ArrayList<INodeReference>();
	private Collection<INodeReference> interceptors = new ArrayList<INodeReference>();

	private Collection<Excluded> excluded = new ArrayList<Excluded>();

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
			XModelObject scan = beansXML.getChildByPath("Scan");
			if(scan != null) {
				loadScan(scan);
			}
		}
	}

	void loadScan(XModelObject scan) {
		XModelObject[] cs = scan.getChildren();
		for (XModelObject c: cs) {
			if("exclude".equals(c.getAttributeValue(XModelObjectConstants.ATTR_ELEMENT_TYPE))) {
				String name = c.getAttributeValue(XModelObjectConstants.ATTR_NAME);
				if(name == null || name.startsWith("!")) continue; //not supported
				Excluded excluded = new Excluded();
				excluded.setFilter(name);
				XModelObject[] cs2 = c.getChildren();
				for (XModelObject c2: cs2) {
					String name2 = c2.getAttributeValue(XModelObjectConstants.ATTR_NAME);
					String t2 = c2.getAttributeValue(XModelObjectConstants.ATTR_ELEMENT_TYPE);
					if("if-class-available".equals(t2)) {
						if(name2.startsWith("!")) {
							excluded.addNotAvailableType(name2.substring(1));
						} else {
							excluded.addAvailableType(name2);
						}						
					} else if("if-class-not-available".equals(t2)) {
						excluded.addNotAvailableType(name2);
					}
				}
				this.excluded.add(excluded);
			}
		}
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	public Collection<INodeReference> getTypeAlternatives() {
		return typeAlternatives;
	}

	public Collection<INodeReference> getStereotypeAlternatives() {
		return stereotypeAlternatives;
	}

	public Collection<INodeReference> getDecorators() {
		return decorators;
	}

	public Collection<INodeReference> getInterceptors() {
		return interceptors;
	}

	public Collection<Excluded> getExcluded() {
		return excluded;
	}


}
