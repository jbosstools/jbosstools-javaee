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
package org.jboss.tools.struts.ui.bot.test.utils;

import org.eclipse.gef.EditPart;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.hamcrest.Description;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class PartMatcher extends AbstractMatcher<EditPart> {

    private final String name;

    public PartMatcher(String name) {
        assert name != null;
        assert name.trim().length() > 0;
        this.name = name;
    }

    @Override
    protected boolean doMatch(Object item) {
        EditPart ep = (EditPart) item;
        IProcessItem ipi = (IProcessItem) ep.getModel();
        return name.equalsIgnoreCase(ipi.getName());
    }

    public void describeTo(Description d) {
        d.appendText("Edit Part with name: " + name);
    }
}
