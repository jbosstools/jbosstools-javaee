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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.common.model.Messages;

public class WizardBot {

    private SWTBot bot;

    public WizardBot(SWTBotShell bot) {
        this.bot = bot.bot();
    }

    public void setName(String name) {
        bot.textWithLabel("Name*").setText(name);
    }

    public void setTemplate(String template) {
        bot.comboBoxWithLabel("Template").setSelection(template);
    }

    public void setTemplate(int index) {
        bot.comboBoxWithLabel("Template").setSelection(index);
    }

    public void next() {
        bot.button(Messages.SpecialWizardSupport_NextArrow).click();
    }

    public void cancel() {
        clickAndWait(Messages.SpecialWizardSupport_Cancel);
    }

    public void finish() {
        clickAndWait(Messages.SpecialWizardSupport_Finish);
    }

    private void clickAndWait(String buttonText) {
        SWTBotShell sh = bot.activeShell();
        bot.button(buttonText).click();
        bot.waitUntil(Conditions.shellCloses(sh), 10000);
        bot.sleep(100);
    }
}
