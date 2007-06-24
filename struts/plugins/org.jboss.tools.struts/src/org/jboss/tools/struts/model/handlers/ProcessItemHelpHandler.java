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
package org.jboss.tools.struts.model.handlers;

import java.util.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.help.*;

public class ProcessItemHelpHandler extends HelpHandler implements StrutsConstants {

    public ProcessItemHelpHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        String key = object.getModelEntity().getName() + "_" + object.getAttributeValue(ATT_TYPE) + "_" + object.getAttributeValue(ATT_SUBTYPE);
        super.help(object.getModel(), key);
    }

}
