/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.quickfix.test;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.OpenOnOptionsDialog;
import org.junit.Test;

public class QuickFixProposalsDescriptionTest extends CDITestBase {

	@Override
	public String getProjectName() {
		return "CDIQuickFixProposals";
	}

	@Test
	public void testAddedCode() {
		
		String className = "AddCodeBean.java";
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), className).toTextEditor());
		
		OpenOnOptionsDialog openOnDialog = quickFixHelper.openOnDialog(
				"AddCodeBean", className);
		assertNotNull(openOnDialog);
		
		String proposeText = null;
		
		for (SWTBotTableItem ti : openOnDialog.getAllOptions()) {
			if (ti.getText().contains("Add java.io.Serializable")) {
				proposeText = openOnDialog.setProposalOption(ti);
				break;
			}
		}
		
		assertNotNull(proposeText);
		
		List<String> affectedLinesInProposal = Arrays.asList("import java.io.Serializable;", 
				"implements Serializable");
		
		for (String affectedLine : affectedLinesInProposal) {
			assertTrue(proposeText.contains(affectedLine));
			String text = getEd().getText();
			assertTrue(text.contains(affectedLine));
		}

	}
	
	@Test
	public void testRemovedCode() {
	
		String className = "RemoveCodeBean.java";
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), className).toTextEditor());
		
		OpenOnOptionsDialog openOnDialog = quickFixHelper.openOnDialog(
				"@Disposes String param1, @Observes String param2", className);
		assertNotNull(openOnDialog);
		
		String proposeText = null;
		
		for (SWTBotTableItem ti : openOnDialog.getAllOptions()) {
			if (ti.getText().contains("Delete annotation @Disposes")) {
				proposeText = openOnDialog.setProposalOption(ti);
				break;
			}
		}
		
		assertNotNull(proposeText);
		
		List<String> affectedLinesInProposal = Arrays.
				asList("import javax.enterprise.inject.Disposes;", 
				"@Disposes String param1");
		
		for (String affectedLine : affectedLinesInProposal) {
			assertFalse(proposeText.contains(affectedLine));
			String text = getEd().getText();
			assertFalse(text.contains(affectedLine));
		}
		
	}
	
	@Test
	public void testEditedCode() {
		
		String className = "EditCodeStereotype.java";
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), className).toTextEditor());
		
		OpenOnOptionsDialog openOnDialog = quickFixHelper.openOnDialog(
				"@Named(\"name\")", className);
		assertNotNull(openOnDialog);
		
		String proposeText = null;
		
		for (SWTBotTableItem ti : openOnDialog.getAllOptions()) {
			if (ti.getText().contains("Change annotation '@Named")) {
				proposeText = openOnDialog.setProposalOption(ti);
				break;
			}
		}
		
		assertNotNull(proposeText);
		
		List<String> affectedLinesInProposal = Arrays.
				asList("@Named");
		
		for (String affectedLine : affectedLinesInProposal) {
			assertTrue(proposeText.contains(affectedLine));
			String text = getEd().getText();
			assertTrue(text.contains(affectedLine));
		}
		
		affectedLinesInProposal = Arrays.
				asList("@Named(\"name\")");
		
		for (String affectedLine : affectedLinesInProposal) {
			assertFalse(proposeText.contains(affectedLine));
			String text = getEd().getText();
			assertFalse(text.contains(affectedLine));
		}
		
	}
	
}
