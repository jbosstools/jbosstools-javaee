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
package org.jboss.tools.seam.internal.core.scanner.lib;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

public class LibraryScanner implements IFileScanner {

	public boolean isRelevant(IFile f) {
		if(f.getName().endsWith(".jar")) return true;
		return false;
	}

	public boolean isLikelyComponentSource(IFile f) {
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		if(o == null) return false;
		if(!o.getModelEntity().getName().equals("FileSystemJar")) {
			((FileSystemsImpl)o.getModel().getByPath("FileSystems")).updateOverlapped();
			o = EclipseResourceUtil.getObjectByResource(f);
			if(o == null || !o.getModelEntity().getName().equals("FileSystemJar")) return false;
		}
		if(o.getChildByPath("META-INF/seam.properties") != null) return true;
		if(o.getChildByPath("META-INF/components.xml") != null) return true;
		return false;
	}

	public SeamComponentDeclaration[] parse(IFile f) throws Exception {
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		if(o == null) return null;
		if(!o.getModelEntity().getName().equals("FileSystemJar")) {
			((FileSystemsImpl)o.getModel().getByPath("FileSystems")).updateOverlapped();
			o = EclipseResourceUtil.getObjectByResource(f);
			if(o == null || !o.getModelEntity().getName().equals("FileSystemJar")) return null;
		}
		XModelObject seamProperties = o.getChildByPath("META-INF/seam.properties");
		XModelObject componentsXML = o.getChildByPath("META-INF/components.xml");
		if(componentsXML == null && seamProperties == null) return null;
		
		ArrayList<SeamComponentDeclaration> list = new ArrayList<SeamComponentDeclaration>();

		processJavaClasses(o, list);
		
		if(componentsXML != null) {
			SeamComponentDeclaration[] components = new XMLScanner().parse(componentsXML);
			if(components != null) {
				for (int i = 0; i < components.length; i++) list.add(components[i]);
			}
		}
		if(seamProperties != null) {
			XModelObject[] properties = seamProperties.getChildren();
			for (int i = 0; i < properties.length; i++) {
				String name = properties[i].getAttributeValue("name");
				String value = properties[i].getAttributeValue("value");
				//TODO put that to a component
			}
			//TODO add components to list
		}		
		
		return list.toArray(new SeamComponentDeclaration[0]);
	}
	
	protected void processJavaClasses(XModelObject o, ArrayList<SeamComponentDeclaration> list) {
		//TODO
	}

}
