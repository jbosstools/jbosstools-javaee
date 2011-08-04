package org.jboss.tools.cdi.ui.test;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.core.test.tck.TCKTest.JavaFileFilter;
import org.jboss.tools.cdi.core.test.tck.TCKTest.PageFileFilter;
import org.jboss.tools.cdi.core.test.tck.TCKTest.XmlFileFilter;
import org.jboss.tools.cdi.core.test.tck.validation.CoreValidationTest;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.test.util.ResourcesUtils;
import org.osgi.framework.Bundle;

public class TCKUITest extends TCKTest {
	public IProject getTestProject() {
		try {
			if(tckProject==null) {
				tckProject = findTestProject();
				if(tckProject==null || !tckProject.exists()) {
					ValidatorManager.setStatus(CoreValidationTest.VALIDATION_STATUS);
					tckProject = importPreparedProject("/");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Can't import CDI test project: " + e.getMessage());
		}

		return tckProject;
	}

	public static IProject importPreparedProject(String packPath) throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if(project==null || !project.exists()) {
			project = ResourcesUtils.importProject(b, PROJECT_PATH);
		}
		String projectPath = project.getLocation().toOSString();
		String resourcePath = FileLocator.resolve(b.getEntry(TCK_RESOURCES_PREFIX)).getFile();
		
		File from = new File(resourcePath + packPath);
		if(from.isDirectory()) {
			File javaSourceTo = new File(projectPath + JAVA_SOURCE_SUFFIX + PACKAGE + packPath);
			FileUtil.copyDir(from, javaSourceTo, true, true, true, new JavaFileFilter());

			File webContentTo = new File(projectPath + WEB_CONTENT_SUFFIX);
			FileUtil.copyDir(from, webContentTo, true, true, true, new PageFileFilter());

			File webInfTo = new File(projectPath + WEB_CONTENT_SUFFIX + WEB_INF_SUFFIX);
			FileUtil.copyDir(from, webInfTo, true, true, true, new XmlFileFilter());
		}
		project.build(IncrementalProjectBuilder.CLEAN_BUILD,null);
		project.build(IncrementalProjectBuilder.FULL_BUILD,null);
		TestUtil._waitForValidation(project);

		return project;
	}
}
