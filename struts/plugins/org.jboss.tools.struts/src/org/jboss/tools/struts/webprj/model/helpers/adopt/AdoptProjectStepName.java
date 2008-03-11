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
package org.jboss.tools.struts.webprj.model.helpers.adopt;

import org.jboss.tools.struts.messages.StrutsUIMessages;

public class AdoptProjectStepName extends AWStep {

    public String getTitle() {
        return StrutsUIMessages.APPLICATION_NAME_AND_WEBXML_FOLDER;
    }

    public String getAttributeMessage(String name) {
        return ("name".equals(name)) ? "Application Name*" :  "web.xml Location*"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void validate() throws Exception {
        context.setWebXMLLocation(support.getAttributeValue(id, "web.xml location")); //$NON-NLS-1$
    }

}

