<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<!--
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
-->
<html:html>
    <head>
        <html:javascript formName="GetNameForm"/>
        <title></title>
    </head>
    <body>
        <html:form action="/greeting.do" onsubmit="return validateGetNameForm(this)">
            Input name:<html:text property="name"/><html:submit value="Say Hello!"/>
         </html:form>
    </body>
</html:html>
