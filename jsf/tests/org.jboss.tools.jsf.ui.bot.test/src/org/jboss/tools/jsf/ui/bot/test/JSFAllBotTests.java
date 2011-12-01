package org.jboss.tools.jsf.ui.bot.test;

import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.CSSSelectorJBIDE3288;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3148and4441Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3577Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3579Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3920Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE4391Test;
import org.jboss.tools.jsf.ui.bot.test.jsf2.refactor.JSF2AttributeRenameTest;
import org.jboss.tools.jsf.ui.bot.test.jsf2.refactor.JSF2MoveParticipantTest;
import org.jboss.tools.jsf.ui.bot.test.jsf2.refactor.JSF2RenameParticipantTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.AddRemoveJSFCapabilitiesTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.CodeCompletionTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.CreateNewJSFProjectTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.FacesConfigCodeCompletionTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.FacesConfigEditingTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.FacesConfigEditingTestJSF2;
import org.jboss.tools.jsf.ui.bot.test.smoke.MarkersTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.OpenOnTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.PropertiesEditorTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.WebXmlEditorTest;
import org.jboss.tools.jsf.ui.bot.test.templates.CreateNewTemplateFromJSFProject;
import org.jboss.tools.jsf.ui.bot.test.templates.SetTemplateForUnknownTagTest;
import org.jboss.tools.jsf.ui.bot.test.templates.UnknownTemplateTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * These are all JSF SWTBot tests for JBDS.
 * 
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses ({
  CreateNewJSFProjectTest.class,    
  AddRemoveJSFCapabilitiesTest.class,
  JBIDE3148and4441Test.class,
  JBIDE4391Test.class,
  JBIDE3577Test.class,
  JBIDE3579Test.class,
  JBIDE3920Test.class,
  UnknownTemplateTest.class,
  SetTemplateForUnknownTagTest.class,
  CSSSelectorJBIDE3288.class,
  JSF2MoveParticipantTest.class,
  JSF2RenameParticipantTest.class,
  JSF2AttributeRenameTest.class,
  OpenOnTest.class,
  CodeCompletionTest.class,
  FacesConfigEditingTest.class,
  FacesConfigEditingTestJSF2.class,
  FacesConfigCodeCompletionTest.class,
  MarkersTest.class,
  WebXmlEditorTest.class,
  CreateNewTemplateFromJSFProject.class,
  PropertiesEditorTest.class
})
public class JSFAllBotTests{
}