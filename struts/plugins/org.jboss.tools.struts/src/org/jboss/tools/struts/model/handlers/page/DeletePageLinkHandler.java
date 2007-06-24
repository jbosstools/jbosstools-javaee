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
package org.jboss.tools.struts.model.handlers.page;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.page.*;

public class DeletePageLinkHandler implements StrutsConstants {

    public DeletePageLinkHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if(new ReplaceConfirmedLinkHelper().replace(object, "", null)) p.setProperty("consumed", "true");
    }

}

