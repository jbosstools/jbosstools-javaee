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
package org.jboss.tools.seam.xml.components.model;

import java.util.Map;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.NamespaceMapping;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.w3c.dom.Element;

public class SeamComponentsFileLoader extends SimpleWebFileLoader {
    
    public SeamComponentsFileLoader() {}

    protected XModelObjectLoaderUtil createUtil() {
        return new SeamComponentsLoaderUtil();
    }
    
    protected boolean isCheckingDTD() {
    	return false;
    }
    
    protected boolean isCheckingSchema() {
    	return false;
    }

    protected String loadNamespace(Element element, XModelObject object) {
    	NamespaceMapping namespaceMapping = SeamNamespaces.getInstance(object.getModel().getMetaData()).getNamespaceMapping(element);
    	object.set("namespaceMapping", namespaceMapping.toString());
    	util.setNamespaceMapping(namespaceMapping);
    	
    	return super.loadNamespace(element, object);
    }

    public String serializeObject(XModelObject object) {
    	String rootName = getRootName(object);
        Element element = createRootElement(rootName, null, null);
        SeamNamespaces.getInstance(object.getModel().getMetaData()).validateNamespaces(object, element);
		NamespaceMapping namespaceMapping = NamespaceMapping.load(object.get("namespaceMapping"));
    	util.setNamespaceMapping(namespaceMapping);
        return serializeToElement(element, object);
    }
    
}
