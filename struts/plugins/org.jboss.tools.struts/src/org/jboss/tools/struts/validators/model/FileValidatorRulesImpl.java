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
package org.jboss.tools.struts.validators.model;

import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.common.model.impl.bundle.CountriesHelper;
import org.jboss.tools.jst.web.model.SimpleWebFileImpl;

public class FileValidatorRulesImpl extends SimpleWebFileImpl {
	private static final long serialVersionUID = 6138417845239684494L;

    public FileValidatorRulesImpl() {}

    protected void onSetEntity(String entity) {
        super.onSetEntity(entity);
        CountriesHelper.init(getModel());
    }

    protected RegularChildren createChildren() {
        return new ValidatorGrouppedChildren();
    }

}

