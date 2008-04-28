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
package org.jboss.tools.struts.validators.model.handlers;

import org.jboss.tools.common.meta.action.impl.handlers.PasteHandler;
import org.jboss.tools.struts.validators.model.ValidatorConstants;

public class ValidatorPasteHandler extends PasteHandler implements ValidatorConstants {

    protected String getAttributeName(String entity) {
        return (entity.startsWith(ENT_VAR)) ? "var-name" :
               (entity.startsWith(ENT_FIELD)) ? "property" :
               (entity.startsWith(ENT_CONSTANT)) ? "constant-name" :
               (entity.startsWith(ENT_GLOBAL)) ? null :
               (entity.startsWith(ENT_FORMSET)) ? null :
               "name";
    }

}

