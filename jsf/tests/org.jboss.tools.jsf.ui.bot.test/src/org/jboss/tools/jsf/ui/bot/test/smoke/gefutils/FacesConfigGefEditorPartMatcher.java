/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.smoke.gefutils;

import org.eclipse.gef.EditPart;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.hamcrest.Description;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
/**
 * Matcher for Editor Part within Face Config Diagram Editor
 * @author Vlado Pakan
 *
 */
public class FacesConfigGefEditorPartMatcher extends AbstractMatcher<EditPart> {

  private final String path;

  public FacesConfigGefEditorPartMatcher(String path) {
    assert path != null;
    assert path.trim().length() > 0;
    this.path = path;
  }

  @Override
  protected boolean doMatch(Object item) {
    EditPart ep = (EditPart) item;
    IGroup iGroup = (IGroup) ep.getModel();
    return path.equals(iGroup.getPath());
  }

  public void describeTo(Description d) {
    d.appendText("Edit Part with path: " + path);
  }
}
