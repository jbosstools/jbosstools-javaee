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
package org.jboss.tools.struts.validators.model.helpers;

import org.jboss.tools.struts.validators.model.ValidatorConstants;

public class ValidatorsTableHelper extends ConstantsTableHelper {
    static String[] header = new String[]{"name"};

    public String[] getHeader() {
        return header;
    }

    protected String entity() {
        return ValidatorConstants.ENT_VALIDATOR;
    }

}

