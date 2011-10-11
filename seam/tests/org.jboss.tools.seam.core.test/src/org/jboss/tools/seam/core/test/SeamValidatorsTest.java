/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.test;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.common.validation.IValidator;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.core.test.validation.ELValidatorWrapper;
import org.jboss.tools.seam.core.test.validation.IValidatorSupport;
import org.jboss.tools.seam.core.test.validation.SeamCoreValidatorWrapper;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.validation.SeamValidationErrorManager;
import org.jboss.tools.seam.internal.core.validation.SeamValidationMessages;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;
import org.jboss.tools.tests.IMarkerFilter;

public class SeamValidatorsTest extends AbstractResourceMarkerTest {

	public static SeamMarkerFilter SEAM_MARKER_FILTER = new SeamMarkerFilter();

	public SeamValidatorsTest() {
		super("Seam Validator Tests");
	}

	public SeamValidatorsTest(String name) {
		super(name);
	}

	@Override
	protected void copyContentsFile(IFile originalFile, IFile newContentFile) throws CoreException {
		assertTrue(originalFile.exists());
		assertTrue(newContentFile.exists());
		super.copyContentsFile(originalFile, newContentFile);
		if("xml".equalsIgnoreCase(originalFile.getFileExtension())) {
			originalFile.setLocalTimeStamp(originalFile.getModificationStamp() + 3000);
		}
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("SeamWebWarTestProject");
		if(!project.exists()) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.seam.core.test",
					"projects/SeamWebWarTestProject",
					"SeamWebWarTestProject");
			project = setup.importProject();
		}
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	@Override
	protected void tearDown() throws Exception {
	}

	private ISeamProject getSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}

	/**
	 * Test for https://jira.jboss.org/jira/browse/JBIDE-6176
	 * @throws CoreException 
	 * @throws ValidationException 
	 */
	public void testFactory() throws CoreException, ValidationException {
		IFile componentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/FactoryTest.java");
		copyContentsFile(componentFile, "src/action/org/domain/SeamWebWarTestProject/session/FactoryTest.new");

		SeamCoreValidatorWrapper seamValidator = new SeamCoreValidatorWrapper(project);
		seamValidator.validate(componentFile);

		assertTrue("Error marker not found", seamValidator.isMessageCreated(SeamValidationMessages.UNKNOWN_FACTORY_NAME, new String[]{"somethings"}));
		copyContentsFile(componentFile, "src/action/org/domain/SeamWebWarTestProject/session/FactoryTest.original");
	}

	/**
	 * Test for https://jira.jboss.org/jira/browse/JBIDE-784
	 * @throws CoreException 
	 */
	public void testJavaFileOutsideClassPath() throws CoreException {
		IFile file = project.getFile("WebContent/Authenticator.java");
		String[] messages = getMarkersMessage(file, SEAM_MARKER_FILTER);
		assertTrue("Problem marker was found in WebContent/Authenticator.java file. Seam EL validator should not validate it.", messages.length == 0);
	}

	/**
	 * Test for http://jira.jboss.com/jira/browse/JBIDE-1318
	 * @throws CoreException 
	 */
	public void testJBIDE1318() throws CoreException {
		getSeamProject(project);
		IFile testJSP = project.getFile("WebContent/test.jsp");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertMarkerIsNotCreated(testJSP, MARKER_TYPE, "actor cannot be resolved");
	}

	public void testVarAttributes() throws CoreException {
		// Test for http://jira.jboss.com/jira/browse/JBIDE-999
		IFile file = project.getFile("WebContent/varAttributes.xhtml");
		int number = getMarkersNumberByGroupName(file, SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP);
		assertEquals("Problem marker was found in varAttributes.xhtml file. Validator did not recognize 'var' attribute.", 0, number);
	}

	public void testMessageBundles() throws CoreException {
		// Test for https://jira.jboss.org/jira/browse/JBIDE-5089
		IFile file = project.getFile("WebContent/messagesValidation.jsp");
		int number = getMarkersNumberByGroupName(file, SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP);
		assertEquals("Problem marker was found in messagesValidation.jsp file. Validator did not recognize a message bundle.", 0, number);
	}

	public void testJiraJbide1696() throws CoreException, ValidationException {
		//getSeamProject(project);

		// Test for http://jira.jboss.com/jira/browse/JBIDE-1696
		IFile subclassComponentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/SubclassTestComponent.java");
		SeamCoreValidatorWrapper seamValidator = new SeamCoreValidatorWrapper(project);
		seamValidator.validate(subclassComponentFile);

		assertTrue("Error marker not found", seamValidator.isMessageCreated(SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE, new String[]{"testComponentJBIDE1696"}));	

		IFile superclassComponentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/SuperclassTestComponent.java");
		copyContentsFile(superclassComponentFile, "src/action/org/domain/SeamWebWarTestProject/session/SuperclassTestComponent.withRemove");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);

		seamValidator = new SeamCoreValidatorWrapper(project);
		seamValidator.validate(subclassComponentFile);

		assertTrue("We changed super class of component but it still don't see changes.", seamValidator.getMessages().size()==0);
	}

	public void testJiraJbide1631() throws CoreException, ValidationException {
		// Test for http://jira.jboss.com/jira/browse/JBIDE-1631
		IFile jbide1631XHTMLFile =  project.getFile("WebContent/JBIDE-1631.xhtml");
		IFile jbide1631XHTMLFile2 =  project.getFile("WebContent/JBIDE-1631.1");

		copyContentsFile(jbide1631XHTMLFile, jbide1631XHTMLFile2);

		ELValidatorWrapper elValidator = new ELValidatorWrapper(project);
		elValidator.validate(jbide1631XHTMLFile);

		assertTrue("Error marker not found", elValidator.isMessageCreated(ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME, new Object[]{"foo1"}));
		assertTrue("Error marker not found", elValidator.isMessageCreated(ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME, new Object[]{"foo2"}));
	}

	public void testDuplicateComponentNameValidator() throws CoreException, ValidationException {
		copyContentsFile(
				"src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.java",
				"src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.original");		
		IFile bbcComponentFile = project
				.getFile("src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.java");

		SeamCoreValidatorWrapper seamValidator = new SeamCoreValidatorWrapper(project);
		seamValidator.validate(bbcComponentFile);
		assertFalse("Error marker was found", seamValidator.isMessageCreated(
				SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, new Object[]{"abcComponent"}));

		// Duplicate component name
		copyContentsFile(
				"src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.java",
				"src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.2");
		seamValidator = new SeamCoreValidatorWrapper(project);
		seamValidator.validate(bbcComponentFile);		
		assertTrue("Error marker not found", seamValidator.isMessageCreated(
				SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, new Object[]{"abcComponent"}));

		// restore file content
		copyContentsFile(
				"src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.java",
				"src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.original");
	}

	public void testStatefulComponentWithoutRemoveMethodValidator() throws CoreException, ValidationException {
		IFile statefulComponentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java");

		String markerText = "Stateful component \"statefulComponent\" must have a method marked @Remove";

		// Stateful component does not contain @Remove method
		assertMarkerIsCreatedForLine("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java",
				"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.2",
				SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE,
				new Object[]{"statefulComponent"},
				16);
	}

	private void assertMarkerIsCreatedForLine(String target, String newContent, String markerTemplate,
		Object[] parameters,int lineNumber) throws CoreException, ValidationException {
		IValidatorSupport validator = new SeamCoreValidatorWrapper(project);
		assertMarkerIsCreatedForLine(validator, target, newContent, markerTemplate, parameters, lineNumber);
	}

	private void assertMarkerIsCreatedForLine(IValidatorSupport validator,String target, String newContent, String markerTemplate,
			Object[] parameters,int lineNumber) throws CoreException, ValidationException {
		copyContentsFile(
				target,
				newContent);
		assertMarkerIsCreatedForLine(validator,target,markerTemplate,parameters,lineNumber);
	}

	private void assertMarkerIsCreatedForLine(String target, String markerTemplate,
		Object[] parameters,int lineNumber) throws CoreException, ValidationException {
		IValidatorSupport validator = new SeamCoreValidatorWrapper(project);
		assertMarkerIsCreatedForLine(validator, target, markerTemplate, parameters, lineNumber);
	}

	private void assertMarkerIsCreatedForLine(IValidatorSupport validator,String target, String markerTemplate,
			Object[] parameters,int lineNumber) throws CoreException, ValidationException {
		IFile targetFile = project.getFile(target);
		validator.validate(targetFile);
		assertTrue("Error marker not found", validator.isMessageCreated(markerTemplate, parameters));
		assertTrue("Error marker has wrong line number", validator.isMessageCreatedOnLine(markerTemplate, parameters,lineNumber));
//if(!validator.isMessageCreated(markerTemplate, parameters)) {
//	System.out.println("!!!");
////	testDuplicateComponents();
//}
//if(!validator.isMessageCreatedOnLine(markerTemplate, parameters,lineNumber)) {
//	System.out.println("!!!");
////	validator.validate(targetFile);
//}
	}

	private void assertMarkerIsNotCreatedForFile(String target, String newContent, String markerTemplate,
		Object[] parameters) throws CoreException, ValidationException {
		IValidatorSupport validator = new SeamCoreValidatorWrapper(project);
		assertMarkerIsNotCreatedForFile(validator, target, newContent, markerTemplate, parameters);
	}

	private void assertMarkerIsNotCreatedForFile(IValidatorSupport validator,String target, String newContent, String markerTemplate,
			Object[] parameters) throws CoreException, ValidationException {
		copyContentsFile(
				target,
				newContent);
		assertMarkerIsNotCreatedForFile(validator,target,markerTemplate, parameters);
	}

	private void assertMarkerIsNotCreatedForFile(String target, String markerTemplate, Object[] parameters)
		throws ValidationException, CoreException {
		IValidatorSupport validator = new SeamCoreValidatorWrapper(project);
		assertMarkerIsNotCreatedForFile(validator, target, markerTemplate, parameters);
	}

	private void assertMarkerIsNotCreatedForFile(IValidatorSupport validator, String target, String markerTemplate, Object[] parameters)
		throws ValidationException, CoreException {
		IFile targetFile = project.getFile(target);
		validator.validate(targetFile);
		assertFalse("Error marker was found", validator.isMessageCreated(markerTemplate, parameters));
	}

	public void testStatefulComponentWithoutDestroyMethodValidator() throws CoreException, ValidationException {
		// Stateful component does not contain @Destroy method
		assertMarkerIsCreatedForLine("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java",
				"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.3",
				SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY,
				new Object[] {"statefulComponent"},
				16);
	}

	public void testStatefulComponentHasWrongScopeValidator() throws CoreException, ValidationException {
		// Stateful component has wrong scope
		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java",
				"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.4",
				SeamValidationMessages.STATEFUL_COMPONENT_WRONG_SCOPE,
				new Object[] {"statefulComponent"},
				16);
	}

	public void testComponentTypeValidator() throws CoreException, ValidationException {
		// Component class name cannot be resolved to a type
		assertMarkerIsCreatedForLine(
				"WebContent/WEB-INF/components.xml",
				"WebContent/WEB-INF/components.2",
				SeamValidationMessages.UNKNOWN_COMPONENT_CLASS_NAME,
				new Object[] {"org.domain.SeamWebWarTestProject.session.StateComponent"},
				15);
	}

	public void testComponentWithoutSetterValidator() throws CoreException, ValidationException {
		// Component class does not contain setter for property
		assertMarkerIsCreatedForLine(
				"WebContent/WEB-INF/components.xml",
				"WebContent/WEB-INF/components.3",
				SeamValidationMessages.UNKNOWN_COMPONENT_PROPERTY,
				new Object[] {"StatefulComponentWithAbcField", "statefulComponentWithAbcField","abc"},
				16);
	}

	public void testEntityHasWrongScopeValidator() throws CoreException, ValidationException {
		// Entity component has wrong scope
		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/entity/abcEntity.java",
				"src/action/org/domain/SeamWebWarTestProject/entity/abcEntity.2",
				SeamValidationMessages.ENTITY_COMPONENT_WRONG_SCOPE,
				new Object[]{"abcEntity"}, 15);
	}

	final String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java";

	public void testDuplicateDestroyMethodValidator() throws CoreException, ValidationException {
		final String NEW_CONTENT_FILE_NAME6 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.6";
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME, 
				NEW_CONTENT_FILE_NAME6, 
				SeamValidationMessages.DUPLICATE_DESTROY,new Object[]{"destroyMethod"}, 34);
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				SeamValidationMessages.DUPLICATE_DESTROY,new Object[]{"destroyMethod2"}, 39);
	}

	public void testDuplicateCreateMethodValidator() throws CoreException, ValidationException {
		// Duplicate @Create method
		final String NEW_CONTENT_FILE_NAME7 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.7";

		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME7, 
				SeamValidationMessages.DUPLICATE_CREATE,
				new Object[]{"createMethod"},36);
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME, SeamValidationMessages.DUPLICATE_CREATE,
				new Object[]{"createMethod2"}, 41);
	}

	public void testDuplicateUnwrapMethodValidator() throws CoreException, ValidationException {
		// Duplicate @Unwrap method

		final String NEW_CONTENT_FILE_NAME8 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.8";
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME8, 
				SeamValidationMessages.DUPLICATE_UNWRAP,
				new Object[] { "unwrapMethod"}, 40);
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME, SeamValidationMessages.DUPLICATE_UNWRAP,
				new Object[] { "unwrapMethod2"}, 45);
	}

	public void testOnlyJavaBeansAndStatefulSessionBeansSupportDestroyMethodValidator() throws CoreException, ValidationException {
		// Only JavaBeans and stateful session beans support @Destroy methods

		final String NEW_CONTENT_FILE_NAME9 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.9";
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME9, 
				SeamValidationMessages.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN,
				new Object[] {"destroyMethod"}, 
				25);
	}

	public void testOnlyComponentClassCanHaveCreateMethodValidator() throws CoreException, ValidationException {
		// Only component class can have @Create method
		final String NEW_CONTENT_FILE_NAME10 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.10";

		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME10, 
				SeamValidationMessages.CREATE_DOESNT_BELONG_TO_COMPONENT,
				new Object[]{"createMethod"},
				25);
	}

	public void testOnlyComponentClassCanHaveUnwrapMethodValidator() throws CoreException, ValidationException {
		// Only component class can have @Unwrap method

		final String NEW_CONTENT_FILE_NAME11 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.11";

		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME11, 
				SeamValidationMessages.UNWRAP_DOESNT_BELONG_TO_COMPONENT, new Object[] {"unwrapMethod"}, 26);
	}

	public void testOnlyComponentClassCanHaveObserverMethodValidator() throws CoreException, ValidationException {
		// Only component class can have @Observer method
		final String NEW_CONTENT_FILE_NAME12 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.12";

		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME12, 
				SeamValidationMessages.OBSERVER_DOESNT_BELONG_TO_COMPONENT, new Object[] {"observerMethod"}, 26);
	}

	public void testDuplicateRemoveMethodValidator() throws CoreException, ValidationException {
		// Duplicate @Remove method

		final String NEW_CONTENT_FILE_NAME1 = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.1";

		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				NEW_CONTENT_FILE_NAME1, 
				SeamValidationMessages.DUPLICATE_REMOVE,
				new Object[] {"removeMethod1"},
				18);
		assertMarkerIsCreatedForLine(
				TARGET_FILE_NAME,
				SeamValidationMessages.DUPLICATE_REMOVE,
				new Object[]{"removeMethod2"},
				22);
	}

	/**
	 * @param statefulComponentFile
	 * @param string
	 * @param i
	 * @throws CoreException 
	 */
	protected void assertMarkerIsCreated(String targetPath, String newContentPath,
			String pattern, int line) throws CoreException {

		IFile newContentFile = project.getFile(newContentPath);
		IFile targetFile = project.getFile(targetPath);
		copyContentsFile(targetFile, newContentFile);
		assertMarkerIsCreated(targetFile, MARKER_TYPE, pattern, line);
	}

	/**
	 * @param statefulComponentFile
	 * @param string
	 * @param i
	 * @throws CoreException 
	 */
	protected void assertMarkerIsCreated(String targetPath,
			String pattern, int line) throws CoreException {
		IFile targetFile = project.getFile(targetPath);
		assertMarkerIsCreated(targetFile, MARKER_TYPE, pattern, line);
	}

	/**
	 * The validator should check duplicate @Remove methods only in stateful session bean component
	 * This method tests usual component (not stateful sessian bean) with two @Remove methods. It must not have error markers.  
	 * @throws CoreException 
	 */
	public void testDuplicateRemoveMethodInComponent_Validator() throws CoreException {
		getSeamProject(project);
		IFile componentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/UsualComponent.java");
		int number = getMarkersNumberByGroupName(componentFile, SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP);
		assertEquals("Problem marker was found in UsualComponent.java file", 0, number);
	}

	public void testUnknownFactoryNameValidator() throws CoreException, ValidationException {
		assertMarkerIsNotCreatedForFile(
				"src/action/org/domain/SeamWebWarTestProject/session/Component12.java",
				SeamValidationMessages.UNKNOWN_FACTORY_NAME,
				new Object[] {"messageList2"});
		// Unknown factory name
		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/Component12.java",
				"src/action/org/domain/SeamWebWarTestProject/session/Component12.2",
				SeamValidationMessages.UNKNOWN_FACTORY_NAME,
				new Object[] {"messageList2"},
				24);
	}

	public void testDuplicateFactoryNameValidator() throws CoreException, ValidationException {
		assertMarkerIsNotCreatedForFile(
			"src/action/org/domain/SeamWebWarTestProject/session/DuplicateFactory.java",
			SeamValidationMessages.DUPLICATE_VARIABLE_NAME,
			new Object[] {"testFactory1"});

		assertMarkerIsCreatedForLine(
			"src/action/org/domain/SeamWebWarTestProject/session/DuplicateFactory.java",
			"src/action/org/domain/SeamWebWarTestProject/session/DuplicateFactory.1",
			SeamValidationMessages.DUPLICATE_VARIABLE_NAME,
			new Object[] {"testFactory1"},
			16);

		assertMarkerIsCreatedForLine(
			"src/action/org/domain/SeamWebWarTestProject/session/DuplicateFactory.java",
			SeamValidationMessages.DUPLICATE_VARIABLE_NAME,
			new Object[] {"testFactory1"},
			21);
	}

	public void testMultipleDataBinderValidator() throws CoreException, ValidationException {
		assertMarkerIsNotCreatedForFile(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.java",
				SeamValidationMessages.MULTIPLE_DATA_BINDER,
				new Object[] {});

		assertMarkerIsNotCreatedForFile(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.java",
				SeamValidationMessages.MULTIPLE_DATA_BINDER,
				new Object[] {});

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.java",
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.2",
				SeamValidationMessages.MULTIPLE_DATA_BINDER,
				new Object[] {},
				21);

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.java",
				SeamValidationMessages.MULTIPLE_DATA_BINDER,
				new Object[] {},
				24);

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.java",
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.2",
				SeamValidationMessages.MULTIPLE_DATA_BINDER,
				new Object[] {},
				21);

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.java",
				SeamValidationMessages.MULTIPLE_DATA_BINDER,
				new Object[] {},
				24);
	}

	public void testUnknownDataModelNameValidator() throws CoreException, ValidationException {
		IFile selectionTestFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.java");
		IFile selectionIndexTestFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.java");
		// Unknown @DataModel/@Out name

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.java",
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionTest.3",
				SeamValidationMessages.UNKNOWN_DATA_MODEL,
				new Object[] {"messageList2"},
				27);

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.java",
				"src/action/org/domain/SeamWebWarTestProject/session/SelectionIndexTest.3",
				SeamValidationMessages.UNKNOWN_DATA_MODEL,
				new Object[] {"messageList2"},
				27);
	}

	public void testDuplicateVariableName_Validator() throws CoreException, ValidationException {
		modifyPreferences();

		assertMarkerIsNotCreatedForFile(
				"src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.java",
				SeamValidationMessages.DUPLICATE_VARIABLE_NAME,
				new Object[] {"messageList"});

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.java",
				"src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.2",
				SeamValidationMessages.DUPLICATE_VARIABLE_NAME,
				new Object[] {"messageList"},
				36);

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.java",
				SeamValidationMessages.DUPLICATE_VARIABLE_NAME,
				new Object[] {"messageList"},
				41);
	}

	public void testUnknownVariableNameValidator() throws CoreException, ValidationException {
		IFile contextVariableTestFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.java");

		String markerText = "Unknown context variable name: \"messageList5\"";

		assertMarkerIsNotCreated(contextVariableTestFile, MARKER_TYPE, markerText);

		// Unknown variable name
		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.java",
				"src/action/org/domain/SeamWebWarTestProject/session/ContextVariableTest.3",
				SeamValidationMessages.UNKNOWN_VARIABLE_NAME,
				new Object[]{"messageList5"}, 22);
	}

	public void testContextVariableCannotBeResolvedValidator() throws CoreException, ValidationException {
		modifyPreferences();

		assertMarkerIsNotCreatedForFile(new ELValidatorWrapper(project),
				"WebContent/abcComponent.xhtml",
				ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
				new Object[]{"bcComponent"});

		// Context variable cannot be resolved
		assertMarkerIsCreatedForLine(new ELValidatorWrapper(project),
				"WebContent/abcComponent.xhtml",
				"WebContent/abcComponent.2",
				ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
				new Object[]{"bcComponent"}, 22);
	}

	public void testPropertyCannotBeResolvedValidator() throws CoreException, ValidationException {
		assertMarkerIsNotCreatedForFile(
			new ELValidatorWrapper(project),
			"WebContent/abcComponent.xhtml",
			ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
			new Object[]{"actionType2"});

		// Property cannot be resolved
		assertMarkerIsCreatedForLine(
			new ELValidatorWrapper(project),
			"WebContent/abcComponent.xhtml",
			"WebContent/abcComponent.3",
			ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
			new Object[]{"actionType2"},
			22);
	}

	public void testPropertyHasOnlySetterValidator() throws CoreException, ValidationException {
		// Unpaired Getter/Setter
		try {
			enableUnpairGetterOrSetterValidation(true);
			String target = "src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.java";
			copyContentsFile(
					target,
					"src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.2");
			copyContentsFile(
					"WebContent/abcComponent.xhtml",
					"WebContent/abcComponent.4");
//I am not sure that we need build here. If test is stable, lets remove this.
//			project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
//			project.build(IncrementalProjectBuilder.FULL_BUILD, null);
//			JobUtils.waitForIdle();

			IFile targetFile = project.getFile(target);
			ELValidatorWrapper wrapper = new ELValidatorWrapper(project);
			wrapper.validate(targetFile);
//			assertMarkerIsNotCreatedForFile(
//					new ELValidatorWrapper(project),
//					"src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.java",
//					"src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.2",
//					ELValidationMessages.UNPAIRED_GETTER_OR_SETTER,
//					new Object[] {"actionType","Setter","Getter"},
//					true);

			assertMarkerIsCreatedForLine(
					wrapper,
					"WebContent/abcComponent.xhtml",
					"WebContent/abcComponent.4",
					ELValidationMessages.UNPAIRED_GETTER_OR_SETTER,
					new Object[] {"actionType","Setter","Getter"},
					22);
		} finally {
			enableUnpairGetterOrSetterValidation(false);
		}
	}

	public void testPropertyHasOnlyGetterValidator() throws CoreException, ValidationException {
		//I am not sure that we need build here. If test is stable, lets remove this.
//		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		try {
			enableUnpairGetterOrSetterValidation(true);
			String target = "src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.java";
			copyContentsFile(
					target,
					"src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.3");
			JobUtils.waitForIdle();

			IFile targetFile = project.getFile(target);
			ELValidatorWrapper wrapper = new ELValidatorWrapper(project);
			wrapper.validate(targetFile);
//			assertMarkerIsNotCreatedForFile(
//					new ELValidatorWrapper(project),
//					"src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.java",
//					"src/action/org/domain/SeamWebWarTestProject/session/AbcComponent.3",
//					ELValidationMessages.UNPAIRED_GETTER_OR_SETTER,
//					new Object[] {"actionType", "Getter", "Setter"}, 
//					true);

			assertMarkerIsCreatedForLine(
					wrapper,
					"WebContent/abcComponent.xhtml",
					"WebContent/abcComponent.original",
					ELValidationMessages.UNPAIRED_GETTER_OR_SETTER,
					new Object[] {"actionType", "Getter", "Setter"},
					22);
		} finally {
			enableUnpairGetterOrSetterValidation(false);
		}
	}

	private void enableUnpairGetterOrSetterValidation(boolean enable) {
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		store.putValue(ELSeverityPreferences.UNPAIRED_GETTER_OR_SETTER, enable?SeamPreferences.ERROR:SeamPreferences.IGNORE);
		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

	public void testInheritedMethods() throws CoreException {
		ISeamProject seamProject = getSeamProject(project);

		ISeamComponent c = seamProject.getComponent("inheritedComponent");
		assertNotNull("Component inheritedComponent is not found", c);

		Set<ISeamComponentMethod> ms = c.getMethodsByType(SeamComponentMethodType.DESTROY);
		assertTrue("Seam tools does not see @Destroy-annotated method declared in super class", ms.size() > 0);

		ms = c.getMethodsByType(SeamComponentMethodType.REMOVE);
		assertTrue("Seam tools does not see @Remove-annotated method declared in super class", ms.size() > 0);

		IFile f = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/InheritedComponent.java");
		int errorsCount = getMarkersNumber(f);
		assertEquals("Seam tools validator does not see annotated methods declared in super class", 0, errorsCount);
	}

	// See https://jira.jboss.org/jira/browse/JBIDE-4393
	public void testDuplicateComponents() throws CoreException, ValidationException {
		assertMarkerIsCreatedForLine(
				"WebContent/WEB-INF/components.xml",
				"WebContent/WEB-INF/duplicateComponents.test",
				SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE,
				new Object[] {"duplicateJavaAndXmlComponentName"},
				5);

		assertMarkerIsCreatedForLine(
				"WebContent/WEB-INF/components.xml",
				SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE,
				new Object[] {"duplicateJavaAndXmlComponentName"},
				8);

		assertMarkerIsCreatedForLine(
				"src/action/org/domain/SeamWebWarTestProject/session/DuplicateComponent.java",
				SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE,
				new Object[] {"duplicateJavaAndXmlComponentName"},
				5);
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-6352
	 * @throws CoreException
	 * @throws ValidationException
	 */
	public void testErrorMarkerForEL() throws CoreException, ValidationException{
		assertMarkerIsCreatedForLine(
			new ELValidatorWrapper(project),
			"WebContent/markerTest.xhtml",
			ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
			new Object[] {"testtt"},
			9);
	}

	public void testErrorMarkerInPagesXML() throws CoreException, ValidationException {
		SeamCoreValidatorWrapper seamValidator = new SeamCoreValidatorWrapper(project);

		assertMarkerIsCreatedForLine(
				seamValidator, 
				"WebContent/WEB-INF/pages.xml", 
				SeamValidationMessages.UNRESOLVED_VIEW_ID, 
				new Object[] {"/home1.xhtml"}, 
				14);
		assertMarkerIsCreatedForLine(
				seamValidator, 
				"WebContent/WEB-INF/pages.xml", 
				SeamValidationMessages.UNRESOLVED_VIEW_ID, 
				new Object[] {"/home2.xhtml"}, 
				17);
		assertMarkerIsCreatedForLine(
				seamValidator, 
				"WebContent/WEB-INF/pages.xml", 
				SeamValidationMessages.UNRESOLVED_VIEW_ID, 
				new Object[] {"/home1.xhtml"}, 
				22);
		assertMarkerIsCreatedForLine(
				seamValidator, 
				"WebContent/WEB-INF/pages.xml", 
				SeamValidationMessages.UNRESOLVED_VIEW_ID, 
				new Object[] {"/home2.xhtml"}, 
				23);
	}

	public static int getMarkersNumber(IResource resource) {
		return getMarkersNumberByGroupName(resource, SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP);
	}

	private static boolean findLine(Integer[] lines, int number) {
		for (int i = 0; i < lines.length; i++) {
			if(lines[i]==number) {
				return true;
			}
		}
		return false;
	}

	private void modifyPreferences(){
		IPreferenceStore store = SeamCorePlugin.getDefault().getPreferenceStore();
		store.putValue(SeamPreferences.UNKNOWN_VARIABLE_NAME, SeamPreferences.ERROR);

		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}

		store = WebKbPlugin.getDefault().getPreferenceStore();
		store.putValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, SeamPreferences.ERROR);
		store.putValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, SeamPreferences.ERROR);
		//store.putValue(ELSeverityPreferences.UNPAIRED_GETTER_OR_SETTER, SeamPreferences.ERROR);
		store.putValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, SeamPreferences.ENABLE);

		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

	private void modifyPreference(String name, String value){
		IPreferenceStore store = SeamCorePlugin.getDefault().getPreferenceStore();

		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}

		store = WebKbPlugin.getDefault().getPreferenceStore();
		store.putValue(name, value);

		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

	public static class SeamMarkerFilter implements IMarkerFilter {
		public boolean accept(IMarker marker) {
			String groupName = marker.getAttribute("groupName", null);
			return groupName!=null && (groupName.equals(SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP) || groupName.equals(IValidator.MARKED_RESOURCE_MESSAGE_GROUP));
		}
	}
}