package org.jboss.tools.cdi.ui.test.validation.java;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.base.test.validation.java.BaseAsYouTypeInJavaValidationTest;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class CDIAsYouTypeInJavaValidationTest extends TCKTest {
	private static final String PAGE_NAME = "JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/el/TestBean.java";

	private BaseAsYouTypeInJavaValidationTest baseTest;
	protected IProject project;

	
	private static final String [][] EL2VALIDATE = 
		{ 
			{"#{namedBean.foos}", "\"foos\" cannot be resolved"}, 
			{"#{snamedBean.foo}", "\"snamedBean\" cannot be resolved"},
			{"#{['}", "EL syntax error: Expecting expression."}
		};

	public void setUp() throws Exception {
		project = TCKTest.importPreparedProject("/");
		baseTest = new BaseAsYouTypeInJavaValidationTest(project);
	}

	public void testAsYouTypeInJavaValidation() throws JavaModelException {
		assertNotNull("Test project '" + TCKTest.MAIN_PROJECT_NAME + "' is not prepared", project);
		baseTest.openEditor(PAGE_NAME);
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		String defaultValidateUnresolvedEL = SeverityPreferences.ENABLE;
		String defaultUnknownELVariableName = SeverityPreferences.IGNORE;
		try {
			defaultValidateUnresolvedEL = store.getString(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL);
			defaultUnknownELVariableName = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, SeverityPreferences.ENABLE);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, SeverityPreferences.ERROR);
			for (int i = 0; i < EL2VALIDATE.length; i++) {
				baseTest.doAsYouTipeInJavaValidationTest(EL2VALIDATE[i][0], EL2VALIDATE[i][1]);
			}
		} finally {
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, defaultValidateUnresolvedEL);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, defaultUnknownELVariableName);
			baseTest.closeEditor();
		}
	}

}
