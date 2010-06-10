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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

/**
 * Drag and Drop implementation based on the last patch available
 * at https://bugs.eclipse.org/bugs/show_bug.cgi?id=285271
 *
 * @author jlukas
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=285271#c10">Original Patch</a>
 */
public class DndSupport {

    private static final Logger L = Logger.getLogger(DndSupport.class.getName());
    private static final int robotDelay = Integer.getInteger("org.jboss.tools.struts.ui.bot.test.dndDelay", 150);

    public static void dnd(final TreeItem ti, final TreeItem ti2) {
        Rectangle r1 = UIThreadRunnable.syncExec(new Result<Rectangle>() {

            public Rectangle run() {
                return ti.getDisplay().map(ti.getParent(), null, ti.getBounds());
            }
        });
        final Point slightOffset = Geometry.add(Geometry.getLocation(r1),
                new Point(10, 10));

        Rectangle r2 = UIThreadRunnable.syncExec(new Result<Rectangle>() {

            public Rectangle run() {
                return ti2.getDisplay().map(ti2.getParent(), null,
                        ti2.getBounds());
            }
        });

        doDragAndDrop(Geometry.min(Geometry.centerPoint(r1), slightOffset),
                Geometry.centerPoint(r2));
    }

    public static void dnd(final TreeItem ti, final FigureCanvas fc) {
        Rectangle r1 = UIThreadRunnable.syncExec(new Result<Rectangle>() {

            public Rectangle run() {
                return ti.getDisplay().map(ti.getParent(), null, ti.getBounds());
            }
        });
        final Point slightOffset = Geometry.add(Geometry.getLocation(r1),
                new Point(10, 10));

        Rectangle r2 = UIThreadRunnable.syncExec(new Result<Rectangle>() {

            public Rectangle run() {
                return fc.getDisplay().map(fc.getParent(), null, fc.getBounds());
            }
        });

        doDragAndDrop(Geometry.min(Geometry.centerPoint(r1), slightOffset),
                Geometry.centerPoint(r2));
    }

    public static void dnd(final TreeItem ti, final Control fc, final int x,
            final int y) {
        Rectangle r1 = UIThreadRunnable.syncExec(new Result<Rectangle>() {

            public Rectangle run() {
                return ti.getDisplay().map(ti.getParent(), null, ti.getBounds());
            }
        });
        final Point slightOffset = Geometry.add(Geometry.getLocation(r1),
                new Point(10, 10));

        Point r2 = UIThreadRunnable.syncExec(new Result<Point>() {

            public Point run() {
                L.info("xxx: " + fc.getLocation().x + ":"
                        + fc.getLocation().y);
                return fc.getDisplay().map(fc, null, x, y);
            }
        });

        doDragAndDrop(Geometry.min(Geometry.centerPoint(r1), slightOffset), r2);
    }

    /**
     *
     */
    private static void doDragAndDrop(final Point source, final Point dest) {
        L.info(MessageFormat.format(
                "Drag-and-dropping from ({0},{1}) to ({2},{3})", source.x,
                source.y, dest.x, dest.y));
        try {
            final Robot awtRobot = new Robot();
            awtRobot.setAutoDelay(robotDelay);
            // the x+10 motion is needed to let native functions register a drag
            // detect. It did not work under Windows
            // otherwise and has been reported to be required for linux, too.
            // But I could not test that.
            UIThreadRunnable.syncExec(new VoidResult() {

                public void run() {
                    awtRobot.mouseMove(source.x, source.y);
                    awtRobot.mousePress(InputEvent.BUTTON1_MASK);
                    awtRobot.mouseMove((source.x + 10), source.y);
                }
            });

            // now pause the test until all runnables on the Display thread have
            // run this is necessary for the pick up
            // to register on linux
            waitForIdle(awtRobot);

            UIThreadRunnable.syncExec(new VoidResult() {

                public void run() {
                    awtRobot.mouseMove((dest.x + 10), dest.y);
                    awtRobot.mouseMove(dest.x, dest.y);
                }
            });

            waitForIdle(awtRobot);

            UIThreadRunnable.syncExec(new VoidResult() {

                public void run() {
                    awtRobot.mouseRelease(InputEvent.BUTTON1_MASK);
                }
            });
            waitForIdle(awtRobot);
        } catch (final AWTException e) {
            L.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//ignore
		}
    }

    private static void waitForIdle(final Robot robot) {
        if (SWT.getPlatform().equals("gtk")) {
            robot.waitForIdle();
        }
    }
}
