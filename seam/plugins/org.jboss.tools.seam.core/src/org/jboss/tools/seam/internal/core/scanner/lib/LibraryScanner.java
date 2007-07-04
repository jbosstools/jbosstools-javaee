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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.internal.core.SeamPropertiesDeclaration;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
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

	public LoadedDeclarations parse(IFile f) throws Exception {
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
		
		LoadedDeclarations ds = new LoadedDeclarations();

		processJavaClasses(o, ds);
		
		if(componentsXML != null) {
			LoadedDeclarations ds1 = new XMLScanner().parse(componentsXML, f.getFullPath());
			if(ds1 != null) {
				ds.getComponents().addAll(ds1.getComponents());
				ds.getFactories().addAll(ds1.getFactories());
			}
		}
		if(seamProperties != null) {
			XModelObject[] properties = seamProperties.getChildren();
			Map<String, SeamPropertiesDeclaration> ds1 = new HashMap<String, SeamPropertiesDeclaration>();
			for (int i = 0; i < properties.length; i++) {
				String name = properties[i].getAttributeValue("name");
				String value = properties[i].getAttributeValue("value");
				int q = name.lastIndexOf('.');
				if(q < 0) continue;
				String componentName = name.substring(0, q);
				String propertyName = name.substring(q + 1);
				SeamPropertiesDeclaration d = ds1.get(componentName);
				if(d == null) {
					d = new SeamPropertiesDeclaration();
					d.setId(properties[i]);
					d.setSourcePath(f.getFullPath());
					d.setName(componentName);
					ds1.put(componentName, d);
				}
				d.addStringProperty(propertyName, value);
			}
			ds.getComponents().addAll(ds1.values());
		}		
		
		return ds;
	}
	
	protected void processJavaClasses(XModelObject o, LoadedDeclarations ds) {
		//TODO
	}

}
