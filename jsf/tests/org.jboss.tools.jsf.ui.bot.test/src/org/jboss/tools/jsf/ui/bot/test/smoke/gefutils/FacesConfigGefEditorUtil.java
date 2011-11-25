/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.smoke.gefutils;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
/**
 * Utils for Faces Config GEF Editor
 * @author Vlado Pakan
 *
 */
public class FacesConfigGefEditorUtil {
  /**
   * Returns position of gefPart
   * @param gefPart
   * @return
   */
  public static Point getGefPartPosition (SWTBotGefEditPart gefPart) {
    return ((IGroup)gefPart.part().getModel()).getPosition();
  }
  /**
   * Returns gefPart bounds
   * @param gefPart
   * @return
   */
  public static Rectangle getGefPartBounds(SWTBotGefEditPart gefPart) {
    return ((IGroup)gefPart.part().getModel()).getImage().getBounds();
  }
  
  public static Rectangle getEditorControlBounds(final FacesConfigGefEditorBot facesConfigGefEditorBot){
    return UIThreadRunnable.syncExec(new Result<Rectangle>() {
      public Rectangle run() {
        return facesConfigGefEditorBot.getControl().getBounds();
      }
    });

  }
}
