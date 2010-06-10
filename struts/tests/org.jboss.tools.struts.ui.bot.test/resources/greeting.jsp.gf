<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
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
<html>
<head>
    <title>Greeting</title>
</head>
    <body>
        <p>
            <bean:write name="GetNameForm" property="name"/>
        </p>
    </body>
</html>