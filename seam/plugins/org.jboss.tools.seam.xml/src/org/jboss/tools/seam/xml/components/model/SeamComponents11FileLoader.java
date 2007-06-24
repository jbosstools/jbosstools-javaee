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

import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class SeamComponents11FileLoader extends SimpleWebFileLoader {
    
    public SeamComponents11FileLoader() {}

    protected XModelObjectLoaderUtil createUtil() {
        return new SeamComponentsLoaderUtil();
    }
    
    protected boolean isCheckingDTD() {
    	return true;
    }
    
    protected boolean isCheckingSchema() {
    	return false;
    }

}
