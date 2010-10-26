/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GreetingAction extends org.apache.struts.action.Action {

    // Global Forwards
    public static final String GLOBAL_FORWARD_getName = "getName";

    // Local Forwards
    public static final String FORWARD_sayHello = "sayHello";

    public GreetingAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = ((GetNameForm)form).getName();
        String greeting = "Hello, " + name + "!";
        ((GetNameForm) form).setName(greeting);
        return mapping.findForward(FORWARD_sayHello);
    }
}
