package org.jboss.tools.seam.ui.bot.test;

import org.jboss.tools.seam.ui.bot.test.create.CreateActions;
import org.jboss.tools.seam.ui.bot.test.create.CreateConversations;
import org.jboss.tools.seam.ui.bot.test.create.CreateEntities;
import org.jboss.tools.seam.ui.bot.test.create.CreateSeamProjects;
import org.jboss.tools.seam.ui.bot.test.create.CreateForms;
import org.jboss.tools.seam.ui.bot.test.create.DeleteSeamProjects;
import org.jboss.tools.seam.ui.bot.test.misc.GotoComponent;
import org.jboss.tools.seam.ui.bot.test.misc.ReverseEngineering;
import org.jboss.tools.seam.ui.bot.test.validate.ComponentsValidator;
import org.jboss.tools.seam.ui.bot.test.validate.ELExprValidator;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;

/**
 * 
 * This is a swtbot testcase for an eclipse application.
 * 
 */

@RunWith(RequirementAwareSuite.class)
@Suite.SuiteClasses({
	CreateSeamProjects.class,
	ReverseEngineering.class,
	ComponentsValidator.class,
	ELExprValidator.class,
	CreateForms.class,
	CreateActions.class,
	CreateConversations.class,
	CreateEntities.class,
	GotoComponent.class,
	DeleteSeamProjects.class
	})
public class SeamAllBotTests {
	
}